<script setup>
import { reactive, ref } from 'vue'
import { mdiBallotOutline, mdiPlus, mdiDelete } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseDivider from '@/components/BaseDivider.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import { api } from '@/plugins/axios.js'

const receiptTypes = [
  { id: 'import', label: 'Phiếu nhập hàng' },
  { id: 'export', label: 'Phiếu xuất hàng' },
]

const form = reactive({
  type: receiptTypes[0],
  employee_id: '',
  warehouse_id: '',
  import_date: '',
  export_date: '',
  export_reason: '',
  batches: [],
})

const addBatch = () => {
  form.batches.push({
    product_id: '',   // Product ID
    variant_id: '',   // Variant ID
    product_name: '',
    quantity: '',
    unit_price: '',
    supplier: '',
    manufacture: '',
    expiry_date: '',
    batch_id: '',
  })
}

const removeBatch = (i) => {
  form.batches.splice(i, 1)
}

const formModalActive = ref(false)
const formModalOkActive = ref(false)
const formMessage = ref('')

const submit = async () => {
  const today = new Date().toISOString().split('T')[0];

  if (!form.employee_id || !form.warehouse_id) {
    formMessage.value = 'Vui lòng nhập nhân viên và kho!';
    formModalActive.value = true;
    return;
  }

  for (const [i, batch] of form.batches.entries()) {
    if (!batch.product_id || !batch.variant_id || !batch.product_name) {
      formMessage.value = `Lô hàng #${i + 1}: sản phẩm, variant và tên sản phẩm không được để trống!`;
      formModalActive.value = true;
      return;
    }

    if (batch.quantity <= 0) {
      formMessage.value = `Lô hàng #${i + 1}: số lượng phải > 0!`;
      formModalActive.value = true;
      return;
    }

    if (batch.expiry_date && batch.expiry_date < today) {
      formMessage.value = `Lô hàng #${i + 1}: ngày hết hạn không hợp lệ!`;
      formModalActive.value = true;
      return;
    }
  }

  try {
    const accessToken = localStorage.getItem('accessToken') || ''
    for (const batch of form.batches) {
      const payload = {
        variantId: batch.variant_id,
        productId: batch.product_id,
        warehouseId: form.warehouse_id,
        quantityTotal: batch.quantity,
        manufacture: batch.manufacture,
        supplier: batch.supplier,
        createDate: form.import_date || today,
        expiryDate: batch.expiry_date || null,
      };

      await api.post('/batches', payload, {
        headers: { Authorization: `Bearer ${accessToken}` }
      });
    }

    formMessage.value = 'Các batch đã được tạo thành công!';
    formModalOkActive.value = true;

    // Reset form
    form.batches = [];
    form.employee_id = '';
    form.warehouse_id = '';
    form.import_date = '';
  } catch (err) {
    console.error(err);
    formMessage.value = err.response?.data?.message || 'Đã có lỗi xảy ra! Vui lòng thử lại.';
    formModalActive.value = true;
  }
};
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton
        :icon="mdiBallotOutline"
        title="Tạo Phiếu Nhập / Xuất Kho"
        main
      />

      <CardBox is-form @submit.prevent="submit">
        <FormField label="Loại phiếu">
          <FormControl v-model="form.type" :options="receiptTypes" />
        </FormField>

        <FormField label="Người lập phiếu">
          <FormControl v-model="form.employee_id" placeholder="ID hoặc tên nhân viên" />
        </FormField>

        <FormField v-if="form.type.id === 'import'" label="Kho tiếp nhận">
          <FormControl v-model="form.warehouse_id" placeholder="ID hoặc tên kho" />
        </FormField>

        <FormField v-else label="Kho nguồn">
          <FormControl v-model="form.warehouse_id" placeholder="ID hoặc tên kho" />
        </FormField>

        <FormField v-if="form.type.id === 'import'" label="Ngày nhập">
          <FormControl type="date" v-model="form.import_date" />
        </FormField>
        <FormField v-else label="Ngày xuất">
          <FormControl type="date" v-model="form.export_date" />
        </FormField>

        <FormField v-if="form.type.id === 'export'" label="Mục đích xuất kho">
          <FormControl
            type="select"
            v-model="form.export_reason"
            :options="[
              { id: 'sale', label: 'Bán hàng' },
              { id: 'transfer', label: 'Chuyển kho' },
              { id: 'damage', label: 'Hàng hỏng / NOC' },
              { id: 'return', label: 'Khách trả lại' },
            ]"
          />
        </FormField>

        <BaseDivider />

        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-lg font-bold">Danh sách lô hàng</h2>
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

          <FormField label="Mã sản phẩm">
            <FormControl v-model="batch.product_id" placeholder="ID sản phẩm" />
          </FormField>

          <FormField label="Variant ID">
            <FormControl v-model="batch.variant_id" placeholder="Variant ID" />
          </FormField>

          <FormField label="Tên sản phẩm">
            <FormControl v-model="batch.product_name" placeholder="Tên sản phẩm" />
          </FormField>

          <FormField label="Số lượng">
            <FormControl type="number" v-model="batch.quantity" />
          </FormField>

          <FormField v-if="form.type.id === 'import'" label="Đơn giá nhập">
            <FormControl type="number" v-model="batch.unit_price" />
          </FormField>

          <FormField v-if="form.type.id === 'import'" label="Nhà cung cấp">
            <FormControl v-model="batch.supplier" placeholder="Tên nhà cung cấp" />
          </FormField>

          <FormField v-if="form.type.id === 'import'" label="Nhà sản xuất">
            <FormControl v-model="batch.manufacture" placeholder="Tên nhà sản xuất" />
          </FormField>

          <FormField v-if="form.type.id === 'import'" label="Ngày hết hạn">
            <FormControl type="date" v-model="batch.expiry_date" />
          </FormField>

          <FormField v-else label="Lô hàng xuất">
            <FormControl v-model="batch.batch_id" placeholder="Mã lô hàng xuất" />
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
