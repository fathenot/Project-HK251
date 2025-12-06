<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import { mdiMagnify, mdiEye, mdiPrinter } from '@mdi/js'

const searchQuery = ref('')
const salesHistory = ref([])

const fetchSalesHistory = async () => {
  try {
    salesHistory.value = [
      {
        id: 1,
        invoiceCode: 'HD1234',
        time: '2025-10-25 14:23',
        customer: 'Khách lẻ',
        items: 5,
        total: 247000,
        paymentMethod: 'Tiền mặt',
      },
      {
        id: 2,
        invoiceCode: 'HD1233',
        time: '2025-10-25 14:15',
        customer: 'Nguyễn Văn A',
        items: 3,
        total: 182000,
        paymentMethod: 'Thẻ',
      },
      {
        id: 3,
        invoiceCode: 'HD1232',
        time: '2025-10-25 13:58',
        customer: 'Khách lẻ',
        items: 8,
        total: 356000,
        paymentMethod: 'Tiền mặt',
      },
      {
        id: 4,
        invoiceCode: 'HD1231',
        time: '2025-10-25 13:45',
        customer: 'Lê Thị C',
        items: 2,
        total: 125000,
        paymentMethod: 'Thẻ',
      },
      {
        id: 5,
        invoiceCode: 'HD1230',
        time: '2025-10-25 13:30',
        customer: 'Khách lẻ',
        items: 12,
        total: 542000,
        paymentMethod: 'Tiền mặt',
      },
    ]
  } catch (error) {
    console.error('Lỗi tải lịch sử bán hàng:', error)
  }
}

const filteredHistory = computed(() => {
  if (!searchQuery.value) return salesHistory.value
  const query = searchQuery.value.toLowerCase()
  return salesHistory.value.filter(
    (sale) =>
      sale.invoiceCode.toLowerCase().includes(query) ||
      sale.customer.toLowerCase().includes(query),
  )
})

const totalRevenue = computed(() => {
  return filteredHistory.value.reduce((sum, sale) => sum + sale.total, 0)
})

const viewDetails = (sale) => {
  alert(`Chi tiết hóa đơn: ${sale.invoiceCode}`)
}

const printInvoice = (sale) => {
  alert(`In hóa đơn: ${sale.invoiceCode}`)
}

onMounted(() => {
  fetchSalesHistory()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Dashboard Bán Hàng</h1>
        <p class="text-gray-600 dark:text-gray-400">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>

      <SalesNavTabs />

      <CardBox>
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Lịch Sử Bán Hàng</h2>
          <p class="text-gray-600 dark:text-gray-400">Xem lại các giao dịch đã thực hiện</p>
        </div>

        <FormField class="mb-6">
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo mã hóa đơn, tên khách hàng..."
          />
        </FormField>

        <CardBox has-table>
          <table>
            <thead>
              <tr>
                <th>Mã HĐ</th>
                <th>Thời gian</th>
                <th>Khách hàng</th>
                <th>SL sản phẩm</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="sale in filteredHistory" :key="sale.id">
                <td data-label="Mã HĐ">{{ sale.invoiceCode }}</td>
                <td data-label="Thời gian">{{ sale.time }}</td>
                <td data-label="Khách hàng">{{ sale.customer }}</td>
                <td data-label="SL sản phẩm">{{ sale.items }} món</td>
                <td data-label="Tổng tiền">
                  <span class="font-semibold text-blue-600 dark:text-blue-400">
                    {{ sale.total.toLocaleString('vi-VN') }}₫
                  </span>
                </td>
                <td data-label="Thanh toán">
                  <span
                    class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200"
                  >
                    {{ sale.paymentMethod }}
                  </span>
                </td>
                <td class="before:hidden lg:w-1 whitespace-nowrap">
                  <div class="flex items-center space-x-2">
                    <BaseButton
                      :icon="mdiEye"
                      color="info"
                      small
                      @click="viewDetails(sale)"
                    />
                    <BaseButton
                      :icon="mdiPrinter"
                      color="info"
                      small
                      @click="printInvoice(sale)"
                    />
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </CardBox>

        <div class="mt-4 flex justify-between items-center pt-4 border-t border-gray-200 dark:border-gray-700">
          <span class="text-sm text-gray-600 dark:text-gray-400">
            Tổng: {{ filteredHistory.length }} giao dịch
          </span>
          <span class="text-lg font-semibold text-blue-600 dark:text-blue-400">
            Tổng doanh thu: {{ totalRevenue.toLocaleString('vi-VN') }}₫
          </span>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
