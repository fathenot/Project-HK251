<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiCubeOutline, mdiMagnify, mdiInformation, mdiFilterVariant, mdiClose } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { api } from '@/plugins/axios'

const sampleLots = [
  {
    id: 'LO001',
    productId: 'SP001',
    product: 'Sản phẩm 1',
    warehouse: 'Kho A',
    quantity: 8,
    minQuantity: 10,
    expiry: '2026-05-01',
    manufacturer: 'NSX A',
    supplier: 'NCC X',
    status: 'low_stock',
  },
  {
    id: 'LO002',
    productId: 'SP002',
    product: 'Sản phẩm 2',
    warehouse: 'Kho B',
    quantity: 20,
    minQuantity: 15,
    expiry: '2025-12-01',
    manufacturer: 'NSX B',
    supplier: 'NCC Y',
    status: 'normal',
  },
  {
    id: 'LO003',
    productId: 'SP003',
    product: 'Sản phẩm 3',
    warehouse: 'Kho A',
    quantity: 15,
    minQuantity: 10,
    expiry: '2025-11-20',
    manufacturer: 'NSX C',
    supplier: 'NCC Z',
    status: 'expiring',
  },
]

const lots = ref([])
const filteredLots = ref([])
const searchQuery = ref('')
const showFilters = ref(false)

const filterStatus = ref('all')
const filterWarehouse = ref('all')
const filterQuantity = ref('all')

const statusOptions = [
  { value: 'all', label: 'Tất cả trạng thái' },
  { value: 'normal', label: 'Bình thường' },
  { value: 'low_stock', label: 'Tồn kho thấp' },
  { value: 'expiring', label: 'Sắp hết hạn' },
]

const warehouseOptions = [
  { value: 'all', label: 'Tất cả kho' },
  { value: 'Kho A', label: 'Kho A' },
  { value: 'Kho B', label: 'Kho B' },
  { value: 'Kho C', label: 'Kho C' },
]

const quantityOptions = [
  { value: 'all', label: 'Tất cả số lượng' },
  { value: 'low', label: 'Dưới mức tối thiểu' },
  { value: 'normal', label: 'Trên mức tối thiểu' },
]

const filterLots = () => {
  let results = lots.value

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    results = results.filter(
      (l) =>
        l.id.toLowerCase().includes(q) ||
        l.productId.toLowerCase().includes(q) ||
        l.product.toLowerCase().includes(q) ||
        l.warehouse.toLowerCase().includes(q),
    )
  }

  if (filterStatus.value !== 'all') {
    results = results.filter((lot) => getWarning(lot) === getStatusLabel(filterStatus.value))
  }

  if (filterWarehouse.value !== 'all') {
    results = results.filter((lot) => lot.warehouse === filterWarehouse.value)
  }

  if (filterQuantity.value !== 'all') {
    if (filterQuantity.value === 'low') {
      results = results.filter((lot) => lot.quantity <= lot.minQuantity)
    } else {
      results = results.filter((lot) => lot.quantity > lot.minQuantity)
    }
  }

  filteredLots.value = results
}

const resetFilters = () => {
  filterStatus.value = 'all'
  filterWarehouse.value = 'all'
  filterQuantity.value = 'all'
  filterLots()
}

const getStatusLabel = (statusValue) => {
  const status = statusOptions.find((opt) => opt.value === statusValue)
  return status ? status.label : ''
}

const getWarning = (lot) => {
  const today = new Date()
  const expiryDate = new Date(lot.expiry)
  const diffDays = Math.ceil((expiryDate - today) / (1000 * 60 * 60 * 24))

  if (lot.quantity <= lot.minQuantity) return 'Mức tồn kho thấp'
  if (diffDays <= 30) return 'Sắp hết hạn'
  return 'Bình thường'
}

const getWarningClass = (lot) => {
  const warning = getWarning(lot)
  if (warning === 'Bình thường') return 'status-normal'
  if (warning === 'Mức tồn kho thấp') return 'status-warning'
  return 'status-danger'
}

const viewLot = (lot) => {
  selectedLot.value = lot
  modalActive.value = true
}

const modalActive = ref(false)
const selectedLot = ref(null)

const fetchLots = async () => {
  try {
    const res = await api.get('/lots')
    lots.value = res.data
  } catch (err) {
    console.error('Lỗi fetch lô hàng, dùng dữ liệu mẫu:', err)
    lots.value = sampleLots
  } finally {
    filterLots()
  }
}

const activeFiltersCount = computed(() => {
  let count = 0
  if (filterStatus.value !== 'all') count++
  if (filterWarehouse.value !== 'all') count++
  if (filterQuantity.value !== 'all') count++
  return count
})

onMounted(fetchLots)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiCubeOutline" title="Quản lý kho" />

      <!-- Search and Filter Header -->
      <div class="controls-header">
        <!-- Combined Search and Filter Box -->
        <CardBox class="search-filter-container">
          <div class="search-filter-wrapper">
            <!-- Search Section -->
            <div class="search-section">
              <div class="search-wrapper">
                <BaseIcon :path="mdiMagnify" class="search-icon" />
                <input
                  v-model="searchQuery"
                  @input="filterLots"
                  class="search-input"
                  placeholder="Tìm kiếm theo Mã lô, Mã sản phẩm, Sản phẩm, Kho"
                />
              </div>

              <!-- Filter Toggle Button -->
              <button
                class="filter-toggle-btn"
                :class="{ active: showFilters }"
                @click="showFilters = !showFilters"
              >
                <BaseIcon :path="mdiFilterVariant" class="filter-icon" />
                <span>Bộ lọc</span>
                <span v-if="activeFiltersCount > 0" class="filter-badge">
                  {{ activeFiltersCount }}
                </span>
              </button>
            </div>

            <!-- Filters Panel - Show inside the same CardBox -->
            <div v-if="showFilters" class="filters-panel-inline">
              <div class="filters-header">
                <h3 class="filters-title">Bộ lọc</h3>
                <button class="reset-filters-btn" @click="resetFilters">
                  <BaseIcon :path="mdiClose" class="reset-icon" />
                  Đặt lại
                </button>
              </div>

              <div class="filters-grid">
                <div class="filter-group">
                  <label class="filter-label">Trạng thái</label>
                  <select v-model="filterStatus" @change="filterLots" class="filter-select">
                    <option
                      v-for="option in statusOptions"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.label }}
                    </option>
                  </select>
                </div>

                <div class="filter-group">
                  <label class="filter-label">Kho</label>
                  <select v-model="filterWarehouse" @change="filterLots" class="filter-select">
                    <option
                      v-for="option in warehouseOptions"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.label }}
                    </option>
                  </select>
                </div>

                <div class="filter-group">
                  <label class="filter-label">Số lượng</label>
                  <select v-model="filterQuantity" @change="filterLots" class="filter-select">
                    <option
                      v-for="option in quantityOptions"
                      :key="option.value"
                      :value="option.value"
                    >
                      {{ option.label }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </CardBox>
      </div>

      <!-- Results Summary -->
      <div class="results-summary">
        <span class="results-text">
          Hiển thị <strong>{{ filteredLots.length }}</strong> lô hàng
        </span>
      </div>

      <!-- Table Container -->
      <div class="table-wrapper">
        <table class="inventory-table">
          <thead>
            <tr>
              <th class="col-lot">Mã lô</th>
              <th class="col-product-id">Mã sản phẩm</th>
              <th class="col-product">Sản phẩm</th>
              <th class="col-warehouse">Kho</th>
              <th class="col-quantity">Số lượng</th>
              <th class="col-status">Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="lot in filteredLots" :key="lot.id" class="table-row" @click="viewLot(lot)">
              <td class="cell-lot">{{ lot.id }}</td>
              <td class="cell-product-id">{{ lot.productId }}</td>
              <td class="cell-product">{{ lot.product }}</td>
              <td class="cell-warehouse">{{ lot.warehouse }}</td>
              <td class="cell-quantity">
                <div class="quantity-display">
                  <span class="quantity-value">{{ lot.quantity }}</span>
                  <span class="min-quantity">/{{ lot.minQuantity }}</span>
                </div>
              </td>
              <td class="cell-status">
                <span :class="['status-badge', getWarningClass(lot)]">
                  {{ getWarning(lot) }}
                </span>
              </td>
            </tr>
            <tr v-if="filteredLots.length === 0" class="no-data-row">
              <td colspan="6" class="no-data-cell">
                <BaseIcon :path="mdiInformation" class="no-data-icon" />
                <p>Không có lô hàng nào được tìm thấy</p>
                <button
                  v-if="activeFiltersCount > 0"
                  @click="resetFilters"
                  class="clear-filters-btn"
                >
                  Xóa bộ lọc
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <CardBoxModal v-model="modalActive" title="Chi tiết lô hàng" button-label="Đóng">
        <div v-if="selectedLot" class="lot-details">
          <div class="detail-section">
            <h3 class="detail-title">Thông tin cơ bản</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Mã lô:</span>
                <span class="detail-value">{{ selectedLot.id }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Mã sản phẩm:</span>
                <span class="detail-value">{{ selectedLot.productId }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Sản phẩm:</span>
                <span class="detail-value">{{ selectedLot.product }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Kho:</span>
                <span class="detail-value">{{ selectedLot.warehouse }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h3 class="detail-title">Thông tin tồn kho</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Số lượng hiện tại:</span>
                <span class="detail-value">{{ selectedLot.quantity }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Mức tồn tối thiểu:</span>
                <span class="detail-value">{{ selectedLot.minQuantity }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Trạng thái:</span>
                <span :class="['status-badge', getWarningClass(selectedLot)]">
                  {{ getWarning(selectedLot) }}
                </span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h3 class="detail-title">Thông tin nhà cung cấp</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">Nhà sản xuất:</span>
                <span class="detail-value">{{ selectedLot.manufacturer }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Nhà cung cấp:</span>
                <span class="detail-value">{{ selectedLot.supplier }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">Hạn sử dụng:</span>
                <span class="detail-value">{{ selectedLot.expiry }}</span>
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>

<style scoped>
.controls-header {
  margin-bottom: 1rem;
}

.search-filter-container {
  padding: 1rem;
}

.search-filter-wrapper {
  width: 100%;
}

.search-section {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  margin-bottom: 0;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  flex: 1;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  color: #6b7280;
  width: 1.25rem;
  height: 1.25rem;
}

.search-input {
  width: 100%;
  padding: 0.75rem 0.75rem 0.75rem 2.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-toggle-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  height: fit-content;
}

.filter-toggle-btn:hover {
  border-color: #9ca3af;
}

.filter-toggle-btn.active {
  background-color: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.filter-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.filter-badge {
  background-color: #ef4444;
  color: white;
  border-radius: 50%;
  width: 1.25rem;
  height: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

/* Inline Filters Panel */
.filters-panel-inline {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.filters-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.filters-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.reset-filters-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}

.reset-filters-btn:hover {
  background: #e5e7eb;
}

.reset-icon {
  width: 1rem;
  height: 1rem;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.filter-select {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.2s;
}

.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.results-summary {
  margin-bottom: 1rem;
  padding: 0.5rem 0;
}

.results-text {
  font-size: 0.875rem;
  color: #6b7280;
}

/* Responsive Design */
@media (max-width: 768px) {
  .search-section {
    flex-direction: column;
    gap: 0.75rem;
  }

  .search-wrapper {
    width: 100%;
  }

  .filter-toggle-btn {
    align-self: flex-start;
  }

  .filters-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .search-filter-container {
    padding: 0.75rem;
  }

  .filters-grid {
    gap: 0.75rem;
  }
}

.table-wrapper {
  background: white;
  border-radius: 0.75rem;
  box-shadow:
    0 1px 3px 0 rgba(0, 0, 0, 0.1),
    0 1px 2px 0 rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.inventory-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

.inventory-table thead {
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.inventory-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #374151;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

.inventory-table td {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.table-row {
  transition: background-color 0.2s;
  cursor: pointer;
}

.table-row:hover {
  background-color: #f9fafb;
}

.col-lot {
  width: 12%;
}

.col-product-id {
  width: 12%;
}

.col-product {
  width: 30%;
}

.col-warehouse {
  width: 16%;
}

.col-quantity {
  width: 15%;
}

.col-status {
  width: 15%;
}

.quantity-display {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.quantity-value {
  font-weight: 600;
  color: #1e40af;
}

.min-quantity {
  font-size: 0.75rem;
  color: #6b7280;
}

.status-badge {
  display: inline-block;
  padding: 0.375rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  text-align: center;
}

.status-normal {
  background-color: #f0fdf4;
  color: #16a34a;
  border: 1px solid #dcfce7;
}

.status-warning {
  background-color: #fffbeb;
  color: #d97706;
  border: 1px solid #fef3c7;
}

.status-danger {
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.no-data-row {
  text-align: center;
}

.no-data-cell {
  padding: 3rem 1rem;
  color: #6b7280;
}

.no-data-icon {
  width: 3rem;
  height: 3rem;
  margin: 0 auto 1rem;
  color: #9ca3af;
}

.clear-filters-btn {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.clear-filters-btn:hover {
  background: #2563eb;
}

.lot-details {
  padding: 0.5rem 0;
}

.detail-section {
  margin-bottom: 1.5rem;
}

.detail-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #1f2937;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
}

.detail-value {
  font-weight: 500;
  color: #1f2937;
}

@media (max-width: 768px) {
  .inventory-table th,
  .inventory-table td {
    padding: 0.75rem 0.5rem;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .col-lot,
  .col-product-id,
  .col-product,
  .col-warehouse,
  .col-quantity,
  .col-status {
    width: auto;
  }
}

@media (max-width: 640px) {
  .table-wrapper {
    border-radius: 0;
    margin: 0 -1rem;
  }

  .inventory-table {
    font-size: 0.75rem;
  }
}
</style>
