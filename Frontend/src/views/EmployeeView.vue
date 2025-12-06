<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiAccount, mdiMagnify, mdiDelete } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

// Dữ liệu fallback
const sampleEmployees = [
  { id: 'EMP001', role: 'warehouse', username: 'messi', name: 'Lionel Messi', hireDate: '2022-07-01' },
  { id: 'EMP002', role: 'sales', username: 'ronaldo', name: 'Cristiano Ronaldo', hireDate: '2023-01-15' },
]

const searchQuery = ref('')
const employees = ref([])

const filteredEmployees = computed(() =>
  employees.value.filter((e) => {
    const q = searchQuery.value.toLowerCase()
    return (
      e.id.toLowerCase().includes(q) ||
      e.username.toLowerCase().includes(q) ||
      e.name.toLowerCase().includes(q)
    )
  }),
)

// Chi tiết modal
const modalActive = ref(false)
const selectedEmployee = ref(null)
const loadingDetail = ref(false)

// Thêm mới modal
const addModalActive = ref(false)
const newEmployee = ref({
  username: '',
  firstName: '',
  lastName: '',
  role: 'sales',
})

// AccessToken
const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

// Fetch employees
const fetchEmployees = async () => {
  try {
    const res = await api.get('/employees', { headers: getTokenHeader(), params: { page: 0, size: 1000 } })
    employees.value = res.data.data.content.map((e) => ({
      id: `EMP${e.id.toString().padStart(3, '0')}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }))
  } catch (err) {
    console.warn('Không thể load nhân viên, dùng dữ liệu mẫu', err)
    employees.value = sampleEmployees
  }
}

// Chi tiết employee
const fetchEmployeeDetail = async (id) => {
  loadingDetail.value = true
  selectedEmployee.value = null
  try {
    const numericId = parseInt(id.replace('EMP', ''))
    const res = await api.get(`/employees/${numericId}`, { headers: getTokenHeader() })
    const e = res.data.data
    selectedEmployee.value = {
      id: `EMP${e.id.toString().padStart(3, '0')}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng dữ liệu mẫu', err)
    selectedEmployee.value = employees.value.find((e) => e.id === id)
  } finally {
    loadingDetail.value = false
  }
}

const viewEmployee = (emp) => {
  modalActive.value = true
  fetchEmployeeDetail(emp.id)
}

// Thêm employee
const openAddModal = () => {
  newEmployee.value = { username: '', firstName: '', lastName: '', role: 'sales' }
  addModalActive.value = true
}

const addEmployee = async () => {
  try {
    const body = { employee: { username: newEmployee.value.username, password: '123456', firstName: newEmployee.value.firstName, lastName: newEmployee.value.lastName, managerId: 0 } }
    let res
    if (newEmployee.value.role === 'warehouse') {
      body.warehouseId = 1
      res = await api.post('/employees/warehouse', body, { headers: getTokenHeader() })
    } else {
      body.storeId = 0
      res = await api.post('/employees/sales', body, { headers: getTokenHeader() })
    }
    const e = res.data.data
    employees.value.push({
      id: `EMP${e.id.toString().padStart(3, '0')}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    })
    addModalActive.value = false
  } catch (err) {
    console.error('Thêm nhân viên lỗi:', err)
    alert('Không thể thêm nhân viên')
  }
}

// Xóa employee
const deleteEmployee = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa nhân viên này?')) return
  try {
    const numericId = parseInt(id.replace('EMP', ''))
    await api.delete(`/employees/${numericId}`, { headers: getTokenHeader() })
    employees.value = employees.value.filter((e) => e.id !== id)
  } catch (err) {
    console.error('Xóa nhân viên lỗi:', err)
    alert('Không thể xóa nhân viên')
  }
}

onMounted(fetchEmployees)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiAccount" title="Tra cứu nhân viên" />

      <!-- Search & Add -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm nhân viên</h3>
            <p class="text-sm text-gray-600">Tìm theo ID, tên hoặc username</p>
          </div>
          <button @click="openAddModal" class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700">
            + Thêm nhân viên
          </button>
          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl v-model="searchQuery" placeholder="Nhập ID, tên hoặc username..." class="pl-10" />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Employee Table -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách nhân viên</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredEmployees.length }} nhân viên
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tên</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Username</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Vai trò</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr v-for="emp in filteredEmployees" :key="emp.id" class="hover:bg-blue-50 cursor-pointer">
                <td class="px-6 py-4" @click="viewEmployee(emp)">{{ emp.id }}</td>
                <td class="px-6 py-4" @click="viewEmployee(emp)">{{ emp.name }}</td>
                <td class="px-6 py-4" @click="viewEmployee(emp)">{{ emp.username }}</td>
                <td class="px-6 py-4" @click="viewEmployee(emp)">{{ emp.role }}</td>
                <td class="px-6 py-4">
                  <button @click.stop="deleteEmployee(emp.id)" class="text-red-600 hover:underline flex items-center gap-1">
                    <svg class="h-4 w-4" viewBox="0 0 24 24" fill="currentColor"><path :d="mdiDelete" /></svg>
                    Xóa
                  </button>
                </td>
              </tr>
              <tr v-if="filteredEmployees.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">Không tìm thấy nhân viên</td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Detail Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết nhân viên" button-label="Đóng" :has-button="true">
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết...</p>
        </div>
        <div v-else-if="selectedEmployee" class="space-y-3">
          <p><strong>ID:</strong> {{ selectedEmployee.id }}</p>
          <p><strong>Tên:</strong> {{ selectedEmployee.name }}</p>
          <p><strong>Username:</strong> {{ selectedEmployee.username }}</p>
          <p><strong>Vai trò:</strong> {{ selectedEmployee.role }}</p>
          <p v-if="selectedEmployee.hireDate"><strong>Ngày tuyển dụng:</strong> {{ selectedEmployee.hireDate }}</p>
        </div>
      </CardBoxModal>

      <!-- Add Modal -->
      <CardBoxModal v-model="addModalActive" title="Thêm nhân viên mới" button-label="Lưu" :has-button="true" @confirm="addEmployee">
        <div class="space-y-4">
          <FormControl v-model="newEmployee.username" placeholder="Username" />
          <FormControl v-model="newEmployee.firstName" placeholder="Họ" />
          <FormControl v-model="newEmployee.lastName" placeholder="Tên" />
          <FormControl v-model="newEmployee.role" :options="[{id:'warehouse', label:'Nhân viên kho'}, {id:'sales', label:'Nhân viên bán hàng'}]" />
        </div>
      </CardBoxModal>

    </SectionMain>
  </LayoutAuthenticated>
</template>
