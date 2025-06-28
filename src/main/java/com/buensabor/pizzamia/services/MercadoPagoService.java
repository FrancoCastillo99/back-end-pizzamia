package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.MercadoPagoDatos;
import com.buensabor.pizzamia.entities.PedidoVenta;
import com.buensabor.pizzamia.entities.PedidoVentaDetalle;
import com.buensabor.pizzamia.repositories.MercadoPagoDatosRepository;
import com.buensabor.pizzamia.repositories.PedidoVentaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;



    @Value("${mercadopago.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private MercadoPagoDatosRepository mercadoPagoDatosRepository;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @PostConstruct
    public void initialize() {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }



    public Preference crearPreferenciaDePago(PedidoVenta pedido) throws MPException, MPApiException {
        try{
            PreferenceClient client = new PreferenceClient();

            List<PreferenceItemRequest> items = new ArrayList<>();

            for (PedidoVentaDetalle detalle : pedido.getDetalles()) {
                String titulo;
                String descripcion;
                BigDecimal precio;

                if (detalle.getArticuloManufacturado() != null) {
                    titulo = detalle.getArticuloManufacturado().getDenominacion();
                    descripcion = detalle.getArticuloManufacturado().getDescripcion();
                    precio = BigDecimal.valueOf(detalle.getArticuloManufacturado().getPrecioVenta());
                } else if (detalle.getArticuloInsumo() != null) {
                    titulo = detalle.getArticuloInsumo().getDenominacion();
                    descripcion = detalle.getArticuloInsumo().getDenominacion();
                    precio = BigDecimal.valueOf(detalle.getArticuloInsumo().getPrecioVenta());
                } else if (detalle.getPromocion() != null) {
                    titulo = "Promoción";
                    descripcion = "Promoción especial";
                    precio = BigDecimal.valueOf(detalle.getPromocion().getPrecio());
                } else {
                    continue;
                }

                PreferenceItemRequest item = PreferenceItemRequest.builder()
                        .id(detalle.getId().toString())
                        .title(titulo)
                        .description(descripcion)
                        .categoryId("food")
                        .quantity(detalle.getCantidad())
                        .currencyId("ARS")
                        .unitPrice(precio)
                        .build();

                items.add(item);
            }

            // Validar que haya ítems
            if (items.isEmpty()) {
                throw new IllegalArgumentException("No se pueden crear preferencias sin ítems");
            }

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(pedido.getCliente().getNombre())
                    .surname(pedido.getCliente().getApellido())
                    .email(pedido.getCliente().getEmail())
                    .identification(IdentificationRequest.builder()
                            .type("EMAIL")
                            .number(pedido.getCliente().getEmail())
                            .build())
                    .build();

            String baseUrl = "https://localhost:5173";

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(baseUrl + "/mercadopago/return?status=approved")
                    .failure(baseUrl + "/mercadopago/return?status=rejected")
                    .pending(baseUrl + "/mercadopago/return?status=pending")
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .externalReference(pedido.getId().toString())
                    .build();

            return client.create(request);
        }catch (MPApiException e) {
            // Imprimir detalles del error para depuración
            System.err.println("Error de API de MercadoPago: " + e.getApiResponse().getContent());
            throw e;
        } catch (Exception e) {
            System.err.println("Error al crear preferencia: " + e.getMessage());
            throw e;
        }

    }

    public boolean verificarPagoAprobado(Long pedidoId) {
        Optional<MercadoPagoDatos> datosPago = mercadoPagoDatosRepository.findByExternalReference(pedidoId.toString());
        return datosPago.isPresent() && "approved".equals(datosPago.get().getStatus());
    }


}
