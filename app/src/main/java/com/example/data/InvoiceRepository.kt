package com.example.data

import kotlinx.coroutines.flow.Flow

class InvoiceRepository(private val invoiceDao: InvoiceDao) {
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    suspend fun insertInvoice(invoice: InvoiceEntity): Long {
        return invoiceDao.insertInvoice(invoice)
    }

    suspend fun deleteInvoice(id: Long) {
        invoiceDao.deleteInvoiceById(id)
    }

    suspend fun getInvoiceById(id: Long): InvoiceEntity? {
        return invoiceDao.getInvoiceById(id)
    }

    fun searchInvoices(query: String): Flow<List<InvoiceEntity>> {
        return invoiceDao.searchInvoices(query)
    }
}
