<script setup>
import { reactive, ref } from 'vue'
import { mdiTruckDelivery, mdiPlus, mdiDelete } from '@mdi/js'
import { api } from '@/plugins/axios'

import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseDivider from '@/components/BaseDivider.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'

const sampleWarehouses = ['Kho A', 'Kho B', 'Kho C']
const sampleCounters = ['Quầy 1', 'Quầy 2', 'Quầy 3']
const sampleBatches = [
  { id: 'LO001', productId: 'SP001', product: 'Sản phẩm 1', warehouse: 'Kho A', quantity: 20 },
  { id: 'LO002', productId: 'SP002', product: 'Sản phẩm 2', warehouse: 'Kho A', quantity: 15 },
  { id: 'LO003', productId: 'SP003', product: 'Sản phẩm 3', warehouse: 'Kho B', quantity: 10 },
]

const form = reactive({
  employee_id: '',
  source_warehouse: '',
  destination_counter: '',
  transfer_date: '',
  batches: [],
})

const addBatch = () => {
  form.batches.push({
    batch_id: '',
    product_id: '',
    product_name: '',
    available_quantity: 0,
    quantity: 0,
  })
}

const removeBatch = (i) => form.batches.splice(i, 1)

const formModalActive = ref(false)
const formModalOkActive = ref(false)
const formMessage = ref('')

const onBatchChange = (batch) => {
  const selected = sampleBatches.find((b) => b.id === batch.batch_id)
  if (selected) {
    batch.product_id = selected.productId
    batch.product_name = selected.product
    batch.available_quantity = selected.quantity
    if (!form.source_warehouse) form.source_warehouse = selected.warehouse
  }
}

// Submit
const submit = async () => {
  if (!form.employee_id || !form.source_warehouse || !form.destination_counter) {
    formMessage.value = 'Vui lòng nhập nhân viên, kho nguồn và quầy nhận!'
    formModalActive.value = true
    return
  }
  for (const [i, batch] of form.batches.entries()) {
    if (!batch.batch_id) {
      formMessage.value = `Lô #${i + 1} chưa chọn!`
      formModalActive.value = true
      return
    }
    if (!batch.quantity || batch.quantity <= 0) {
      formMessage.value = `Lô #${i + 1}: số lượng chuyển phải > 0!`
      formModalActive.value = true
      return
    }
    if (batch.quantity > batch.available_quantity) {
      formMessage.value = `Lô #${i + 1}: số lượng chuyển không được lớn hơn tồn kho (${batch.available_quantity})!`
      formModalActive.value = true
      return
    }
  }

  try {
    const payload = {
      employee_id: form.employee_id,
      source_warehouse: form.source_warehouse,
      destination_counter: form.destination_counter,
      transfer_date: form.transfer_date,
      batches: form.batches.map((b) => ({
        batch_id: b.batch_id,
        product_id: b.product_id,
        quantity: b.quantity,
      })),
    }

    const res = await api.post('/transfer', payload)
    formMessage.value = 'Phiếu chuyển hàng đã được tạo thành công!'
    formModalOkActive.value = true

    // Reset form nếu cần
    form.employee_id = ''
    form.source_warehouse = ''
    form.destination_counter = ''
    form.transfer_date = ''
    form.batches = []
  } catch (err) {
    console.error(err)
    formMessage.value = err.response?.data?.message || 'Lỗi khi tạo phiếu chuyển hàng!'
    formModalActive.value = true
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiTruckDelivery" title="Tạo Phiếu Chuyển Hàng" main />

      <CardBox is-form @submit.prevent="submit">
        <FormField label="Người lập phiếu">
          <FormControl v-model="form.employee_id" placeholder="ID hoặc tên nhân viên" />
        </FormField>

        <FormField label="Kho nguồn">
          <FormControl as="select" v-model="form.source_warehouse">
            <option value="" disabled>-- Chọn kho --</option>
            <option v-for="w in sampleWarehouses" :key="w" :value="w">{{ w }}</option>
          </FormControl>
        </FormField>

        <FormField label="Quầy nhận">
          <FormControl as="select" v-model="form.destination_counter">
            <option value="" disabled>-- Chọn quầy --</option>
            <option v-for="c in sampleCounters" :key="c" :value="c">{{ c }}</option>
          </FormControl>
        </FormField>

        <FormField label="Ngày chuyển">
          <FormControl type="date" v-model="form.transfer_date" />
        </FormField>

        <BaseDivider />

        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-lg font-bold">Danh sách lô hàng chuyển</h2>
          <BaseButton :icon="mdiPlus" color="info" label="Thêm lô hàng" @click="addBatch" />
        </div>

        <div
          v-for="(batch, index) in form.batches"
          :key="index"
          class="mb-6 rounded-lg border bg-gray-50 p-4"
        >
          <div class="mb-2 flex items-center justify-between">
            <h3 class="font-semibold">Lô hàng #{{ index + 1 }}</h3>
            <BaseButton
              color="danger"
              :icon="mdiDelete"
              label="Xóa lô"
              @click="removeBatch(index)"
            />
          </div>

          <BaseDivider />

          <FormField label="Chọn lô hàng">
            <FormControl as="select" v-model="batch.batch_id" @change="onBatchChange(batch)">
              <option value="" disabled>-- Chọn lô --</option>
              <option
                v-for="b in sampleBatches.filter((b) => b.warehouse === form.source_warehouse)"
                :key="b.id"
                :value="b.id"
              >
                {{ b.id }} - {{ b.product }} (Tồn: {{ b.quantity }})
              </option>
            </FormControl>
          </FormField>

          <FormField label="Mã sản phẩm">
            <FormControl v-model="batch.product_id" disabled />
          </FormField>

          <FormField label="Tên sản phẩm">
            <FormControl v-model="batch.product_name" disabled />
          </FormField>

          <FormField label="Tồn kho">
            <FormControl type="number" v-model="batch.available_quantity" disabled />
          </FormField>

          <FormField label="Số lượng chuyển">
            <FormControl type="number" v-model="batch.quantity" />
          </FormField>
        </div>

        <template #footer>
          <BaseButton type="submit" color="info" label="Tạo phiếu" />
        </template>
      </CardBox>

      <CardBoxModal v-model="formModalActive" title="Thông báo" button-label="Cancel">
        <p>{{ formMessage }}</p>
      </CardBoxModal>

      <CardBoxModal v-model="formModalOkActive" title="Thông báo" button-label="OK">
        <p>{{ formMessage }}</p>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
