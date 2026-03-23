<script setup>
import { ref, computed } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import {
  mdiPlus,
  mdiMinus,
  mdiDelete,
  mdiMagnify,
  mdiCashRegister,
  mdiCart,
  mdiCash,
  mdiCreditCard,
} from '@mdi/js'

const searchQuery = ref('')
const barcodeInput = ref('')
const cart = ref([])
const discount = ref(0)

const popularProducts = ref([
  { id: 1, name: 'Sữa tươi Vinamilk', price: 32000, code: 'P001' },
  { id: 2, name: 'Gạo ST25 5kg', price: 125000, code: 'P002' },
  { id: 3, name: 'Nước ngọt Coca Cola', price: 10000, code: 'P003' },
  { id: 4, name: 'Bánh mì sandwich', price: 25000, code: 'P004' },
  { id: 5, name: 'Dầu ăn Simply 1L', price: 45000, code: 'P005' },
  { id: 6, name: 'Mì gói Hảo Hảo', price: 3500, code: 'P006' },
])

const filteredProducts = computed(() => {
  if (!searchQuery.value && !barcodeInput.value) return []
  const query = (searchQuery.value || barcodeInput.value).toLowerCase()
  return popularProducts.value.filter((p) => p.name.toLowerCase().includes(query) || p.code.toLowerCase().includes(query))
})

const cartTotal = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const discountAmount = computed(() => {
  return (cartTotal.value * discount.value) / 100
})

const finalTotal = computed(() => {
  return cartTotal.value - discountAmount.value
})

const addToCart = (product) => {
  const existingItem = cart.value.find((item) => item.id === product.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    cart.value.push({
      ...product,
      quantity: 1,
    })
  }
}

const removeFromCart = (productId) => {
  const index = cart.value.findIndex((item) => item.id === productId)
  if (index !== -1) {
    if (cart.value[index].quantity > 1) {
      cart.value[index].quantity--
    } else {
      cart.value.splice(index, 1)
    }
  }
}

const deleteFromCart = (productId) => {
  cart.value = cart.value.filter((item) => item.id !== productId)
}

const clearCart = () => {
  cart.value = []
  discount.value = 0
}

const checkout = (paymentMethod) => {
  if (cart.value.length === 0) {
    alert('Giỏ hàng trống!')
    return
  }
  alert(`Thanh toán thành công bằng ${paymentMethod}! Tổng tiền: ${finalTotal.value.toLocaleString('vi-VN')}₫`)
  clearCart()
}

const searchProduct = () => {
  if (barcodeInput.value || searchQuery.value) {
    const query = (barcodeInput.value || searchQuery.value).toLowerCase()
    const product = popularProducts.value.find(
      (p) => p.name.toLowerCase().includes(query) || p.code.toLowerCase().includes(query),
    )
    if (product) {
      addToCart(product)
      barcodeInput.value = ''
      searchQuery.value = ''
    } else {
      alert('Không tìm thấy sản phẩm!')
    }
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Dashboard Bán Hàng</h1>
        <p class="text-gray-600 dark:text-gray-400">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>

      <SalesNavTabs />

      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <!-- Left Column: Search and Products -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Search Section -->
          <CardBox>
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-4">
              Quét Mã Vạch / Tìm Sản Phẩm
            </h3>
            <div class="flex space-x-2">
              <FormField class="flex-1">
                <FormControl
                  v-model="barcodeInput"
                  :icon="mdiMagnify"
                  placeholder="Quét mã vạch hoặc nhập tên sản phẩm..."
                  @keyup.enter="searchProduct"
                />
              </FormField>
              <BaseButton label="Tìm" color="info" @click="searchProduct" />
            </div>
          </CardBox>

          <!-- Popular Products -->
          <CardBox>
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">
              Sản Phẩm Bán Chạy
            </h3>
            <p class="text-sm text-gray-600 dark:text-gray-400 mb-4">Nhấn để thêm vào giỏ hàng</p>
            <div class="grid grid-cols-2 gap-4 md:grid-cols-3">
              <div
                v-for="product in popularProducts"
                :key="product.id"
                class="p-4 border rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-slate-800 transition-colors text-center"
                @click="addToCart(product)"
              >
                <h4 class="font-medium text-gray-800 dark:text-white mb-1">{{ product.name }}</h4>
                <p class="text-lg font-bold text-blue-600 dark:text-blue-400">
                  {{ product.price.toLocaleString('vi-VN') }}₫
                </p>
              </div>
            </div>
          </CardBox>
        </div>

        <!-- Right Column: Shopping Cart -->
        <div>
          <CardBox>
            <div class="flex justify-between items-center mb-4">
              <div class="flex items-center space-x-2">
                <BaseIcon :path="mdiCart" size="24" class="text-gray-600 dark:text-gray-400" />
                <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Giỏ Hàng</h3>
              </div>
              <span
                class="px-3 py-1 text-sm rounded-full bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200"
              >
                {{ cart.length }} sản phẩm
              </span>
            </div>

            <div v-if="cart.length === 0" class="text-center py-12">
              <BaseIcon :path="mdiCart" size="64" class="mx-auto text-gray-300 dark:text-gray-600 mb-4" />
              <p class="text-gray-500 dark:text-gray-400">Chưa có sản phẩm</p>
            </div>

            <div v-else class="space-y-3 mb-4 max-h-96 overflow-y-auto">
              <div
                v-for="item in cart"
                :key="item.id"
                class="p-3 rounded-lg bg-gray-50 dark:bg-slate-800"
              >
                <div class="flex justify-between items-start mb-2">
                  <div class="flex-1">
                    <h4 class="font-medium text-gray-800 dark:text-white">{{ item.name }}</h4>
                    <p class="text-sm text-gray-600 dark:text-gray-400">
                      {{ item.price.toLocaleString('vi-VN') }}₫ × {{ item.quantity }}
                    </p>
                  </div>
                  <BaseButton
                    :icon="mdiDelete"
                    color="danger"
                    small
                    @click="deleteFromCart(item.id)"
                  />
                </div>
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-2">
                    <BaseButton
                      :icon="mdiMinus"
                      color="lightDark"
                      small
                      @click="removeFromCart(item.id)"
                    />
                    <span class="font-semibold w-8 text-center">{{ item.quantity }}</span>
                    <BaseButton
                      :icon="mdiPlus"
                      color="lightDark"
                      small
                      @click="addToCart(item)"
                    />
                  </div>
                  <span class="font-bold text-blue-600 dark:text-blue-400">
                    {{ (item.price * item.quantity).toLocaleString('vi-VN') }}₫
                  </span>
                </div>
              </div>
            </div>

            <div v-if="cart.length > 0" class="space-y-4 border-t pt-4">
              <!-- Discount -->
              <FormField label="% Giảm giá">
                <FormControl
                  v-model="discount"
                  type="number"
                  min="0"
                  max="100"
                  placeholder="0"
                />
              </FormField>
              <div class="text-right text-sm text-gray-600 dark:text-gray-400">
                Giảm: {{ discountAmount.toLocaleString('vi-VN') }}₫
              </div>

              <!-- Summary -->
              <div class="space-y-2">
                <div class="flex justify-between text-gray-700 dark:text-gray-300">
                  <span>Tạm tính:</span>
                  <span>{{ cartTotal.toLocaleString('vi-VN') }}₫</span>
                </div>
                <div class="flex justify-between text-xl font-bold text-blue-600 dark:text-blue-400">
                  <span>Tổng cộng:</span>
                  <span>{{ finalTotal.toLocaleString('vi-VN') }}₫</span>
                </div>
              </div>

              <!-- Payment Methods -->
              <div class="grid grid-cols-2 gap-2">
                <BaseButton
                  :icon="mdiCash"
                  label="Tiền mặt"
                  color="lightDark"
                  class="w-full"
                  @click="checkout('Tiền mặt')"
                />
                <BaseButton
                  :icon="mdiCreditCard"
                  label="Thẻ"
                  color="lightDark"
                  class="w-full"
                  @click="checkout('Thẻ')"
                />
              </div>

              <!-- Checkout Button -->
              <BaseButton
                :icon="mdiCashRegister"
                label="Thanh toán"
                color="info"
                class="w-full"
                @click="checkout('Tiền mặt')"
              />
            </div>
          </CardBox>
        </div>
      </div>
    </SectionMain>
  </LayoutAuthenticated>
</template>
