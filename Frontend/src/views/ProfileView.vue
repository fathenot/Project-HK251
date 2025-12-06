<script setup>
import { reactive, ref } from 'vue'
import { mdiAccount, mdiMail, mdiPhone, mdiHome, mdiCalendar, mdiLock } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModalNew.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButtons from '@/components/BaseButtons.vue'
import BaseButton from '@/components/BaseButton.vue'
import { useMainStore } from '@/stores/main'
import { api } from '@/plugins/axios.js'

const mainStore = useMainStore()

const profileForm = reactive({
  name: mainStore.userName || '',
  email: mainStore.userEmail || '',
  phone: mainStore.userPhone || '',
  address: mainStore.userAddress || '',
  hireDate: mainStore.role !== 'customer' ? mainStore.hiredDate : undefined,
  points: mainStore.role === 'customer' ? mainStore.points : undefined,
  role: mainStore.role,
})

const modalActive = ref(false)
const modalMessage = ref('')
const modalTitle = ref('Thông báo')

const editPasswordMode = ref(false)

const showModal = (title, message) => {
  modalTitle.value = title
  modalMessage.value = message
  modalActive.value = true
}

const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)

const submitProfile = async () => {
  if (!profileForm.name) {
    showModal('Lỗi', 'Họ và tên là bắt buộc!')
    return
  }

  // Customer bắt buộc email
  if (mainStore.role === 'customer') {
    if (!profileForm.email) return showModal('Lỗi', 'Email là bắt buộc!')
    if (!validateEmail(profileForm.email)) return showModal('Lỗi', 'Email không hợp lệ!')
  } else {
    if (profileForm.email && !validateEmail(profileForm.email))
      return showModal('Lỗi', 'Email không hợp lệ!')
  }

  if (profileForm.phone && !/^\d{9,15}$/.test(profileForm.phone)) {
    showModal('Lỗi', 'Số điện thoại không hợp lệ!')
    return
  }

  try {
    const payload = { ...profileForm }

    const res = await api.put('/user/profile', payload)

    if (!res.data?.success) {
      showModal('Lỗi', res.data?.message || 'Cập nhật thất bại!')
      return
    }

    mainStore.setUser(res.data.data)

    showModal('Thành công', res.data?.message || 'Thông tin đã được cập nhật!')
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Cập nhật thất bại!')
  }
}

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  passwordConfirmation: '',
})

const submitPassword = async () => {
  if (!passwordForm.currentPassword ||
      !passwordForm.newPassword ||
      !passwordForm.passwordConfirmation) {
    showModal('Lỗi', 'Vui lòng điền đầy đủ các trường mật khẩu!')
    return
  }

  if (passwordForm.password !== passwordForm.password_confirmation) {
    showModal('Lỗi', 'Mật khẩu xác nhận không khớp!')
    return
  }

  try {
    const accessToken = localStorage.getItem('accessToken') || ''
    const res = await api.put(
      '/auth/change-password',
      { ...passwordForm },
      {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      }
    )

    if (!res.data?.success) {
      showModal('Lỗi', res.data?.message || 'Đổi mật khẩu thất bại!')
      return
    }

    showModal('Thành công', 'Đổi mật khẩu thành công!')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.passwordConfirmation = ''
    editPasswordMode.value = false
  } catch (err) {
    showModal('Lỗi', err.response?.data?.message || 'Đổi mật khẩu thất bại!')
  }
}
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain class="flex flex-col gap-6 lg:flex-row">
      <div class="flex-shrink-0">
        <img
          :src="mainStore.userAvatar"
          alt="avatar"
          class="h-32 w-32 rounded-full border shadow-sm"
        />
      </div>

      <div class="flex-1">
        <CardBox>
          <FormField v-if="profileForm.name !== undefined" label="Họ và tên">
            <FormControl v-model="profileForm.name" :icon="mdiAccount" />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Email">
            <FormControl v-model="profileForm.email" :icon="mdiMail" readonly />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Số điện thoại">
            <FormControl v-model="profileForm.phone" :icon="mdiPhone" />
          </FormField>

          <FormField v-if="profileForm.role === 'customer'" label="Địa chỉ">
            <FormControl v-model="profileForm.address" :icon="mdiHome" />
          </FormField>

          <FormField v-if="profileForm.role !== 'customer'" label="Ngày vào làm">
            <FormControl v-model="profileForm.hireDate" :icon="mdiCalendar" readonly />
          </FormField>

          <FormField v-if="profileForm.points !== customer" label="Điểm tích lũy">
            <FormControl v-model="profileForm.points" readonly />
          </FormField>

          <template #footer>
            <BaseButtons class="mt-4">
              <BaseButton color="success" label="Lưu thông tin" @click="submitProfile" />
              <BaseButton color="info" label="Đổi mật khẩu" @click="editPasswordMode = true" />
            </BaseButtons>
          </template>
        </CardBox>
      </div>
    </SectionMain>

    <CardBoxModal v-model="editPasswordMode" title="Đổi mật khẩu">
      <FormField label="Mật khẩu hiện tại">
        <FormControl v-model="passwordForm.currentPassword" type="password" :icon="mdiLock" />
      </FormField>
      <FormField label="Mật khẩu mới">
        <FormControl v-model="passwordForm.newPassword" type="password" :icon="mdiLock" />
      </FormField>
      <FormField label="Xác nhận mật khẩu">
        <FormControl v-model="passwordForm.passwordConfirmation" type="password" :icon="mdiLock" />
      </FormField>

      <div class="mt-10">
        <BaseButtons>
          <BaseButton color="success" label="Đổi mật khẩu" @click="submitPassword" />
        </BaseButtons>
      </div>
    </CardBoxModal>

    <CardBoxModal v-model="modalActive" :title="modalTitle" button="OK">
      <p>{{ modalMessage }}</p>
    </CardBoxModal>
  </LayoutAuthenticated>
</template>
