package com.buensabor.pizzamia.services;

import com.buensabor.pizzamia.entities.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    public byte[] generarFacturaPdf(Factura factura) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Formato para números
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Cabecera de la factura
        agregarCabecera(document, factura, formatoFecha);

        // Información del cliente
        agregarDatosCliente(document, factura.getCliente());

        // Tabla de productos
        agregarTablaProductos(document, factura, formatoMoneda);

        // Información de pago
        agregarInformacionPago(document, factura, formatoMoneda);

        document.close();

        return baos.toByteArray();
    }

    private void agregarCabecera(Document document, Factura factura, DateTimeFormatter formatoFecha) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph titulo = new Paragraph("FACTURA", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph numeroFactura = new Paragraph("Factura Nº: " + factura.getId(), fontSubtitulo);
        numeroFactura.setAlignment(Element.ALIGN_RIGHT);
        document.add(numeroFactura);

        Paragraph fechaFactura = new Paragraph("Fecha: " + factura.getFechaFacturacion().format(formatoFecha), fontSubtitulo);
        fechaFactura.setAlignment(Element.ALIGN_RIGHT);
        document.add(fechaFactura);

        document.add(Chunk.NEWLINE);
    }

    private void agregarDatosCliente(Document document, Cliente cliente) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph tituloCliente = new Paragraph("Datos del Cliente", fontTitulo);
        document.add(tituloCliente);

        document.add(new Paragraph("Nombre: " + cliente.getNombre() + " " + cliente.getApellido(), fontNormal));
        document.add(new Paragraph("Email: " + cliente.getEmail(), fontNormal));
        document.add(new Paragraph("Teléfono: " + cliente.getTelefono(), fontNormal));

        document.add(Chunk.NEWLINE);
    }

    private void agregarTablaProductos(Document document, Factura factura, NumberFormat formatoMoneda) throws DocumentException {
        Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font fontCelda = new Font(Font.FontFamily.HELVETICA, 10);

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1, 5, 2, 2});

        // Encabezados
        PdfPCell celdaEncabezado = new PdfPCell();
        celdaEncabezado.setBackgroundColor(BaseColor.DARK_GRAY);
        celdaEncabezado.setPadding(5);

        celdaEncabezado.setPhrase(new Phrase("Cant.", fontEncabezado));
        tabla.addCell(celdaEncabezado);

        celdaEncabezado.setPhrase(new Phrase("Descripción", fontEncabezado));
        tabla.addCell(celdaEncabezado);

        celdaEncabezado.setPhrase(new Phrase("Precio Unit.", fontEncabezado));
        tabla.addCell(celdaEncabezado);

        celdaEncabezado.setPhrase(new Phrase("Subtotal", fontEncabezado));
        tabla.addCell(celdaEncabezado);

        // Contenido de la tabla
        for (FacturaDetalle detalle : factura.getDetalles()) {
            String descripcion;
            double precioUnitario = detalle.getSubTotal() / detalle.getCantidad();

            if (detalle.getArticuloManufacturado() != null) {
                descripcion = detalle.getArticuloManufacturado().getDenominacion();
            } else if (detalle.getArticuloInsumo() != null) {
                descripcion = detalle.getArticuloInsumo().getDenominacion();
            } else if (detalle.getPromocion() != null) {
                descripcion = "Promoción: " + detalle.getPromocion().getDescuento() + "% descuento";
            } else {
                descripcion = "Producto sin especificar";
            }

            tabla.addCell(new Phrase(detalle.getCantidad().toString(), fontCelda));
            tabla.addCell(new Phrase(descripcion, fontCelda));
            tabla.addCell(new Phrase(formatoMoneda.format(precioUnitario), fontCelda));
            tabla.addCell(new Phrase(formatoMoneda.format(detalle.getSubTotal()), fontCelda));
        }

        document.add(tabla);
        document.add(Chunk.NEWLINE);
    }

    private void agregarInformacionPago(Document document, Factura factura, NumberFormat formatoMoneda) throws DocumentException {
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10);

        PdfPTable tablaTotal = new PdfPTable(2);
        tablaTotal.setWidthPercentage(50);
        tablaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tablaTotal.addCell(new Phrase("Subtotal:", fontNormal));
        tablaTotal.addCell(new Phrase(formatoMoneda.format(factura.getSubTotal()), fontNormal));

        tablaTotal.addCell(new Phrase("Costo de envío:", fontNormal));
        tablaTotal.addCell(new Phrase(formatoMoneda.format(factura.getCostoEnvio()), fontNormal));

        tablaTotal.addCell(new Phrase("TOTAL:", fontTitulo));
        tablaTotal.addCell(new Phrase(formatoMoneda.format(factura.getTotal()), fontTitulo));

        document.add(tablaTotal);

        document.add(Chunk.NEWLINE);

        // Información del método de pago
        Paragraph metodoPago = new Paragraph("Método de pago: " + factura.getPedidoVenta().getTipoPago().toString(), fontNormal);
        document.add(metodoPago);

        // Si es Mercado Pago, agregar información de la transacción
        if (factura.getMpDatos() != null) {
            document.add(new Paragraph("ID de transacción Mercado Pago: " + factura.getMpDatos().getPayment_type_id(), fontNormal));
        }

        // Agregar información del pedido
        document.add(new Paragraph("Pedido Nº: " + factura.getPedidoVenta().getId(), fontNormal));
        document.add(new Paragraph("Tipo de envío: " + factura.getPedidoVenta().getTipoEnvio().toString(), fontNormal));
    }
}
