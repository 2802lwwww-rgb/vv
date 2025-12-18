<template>
  <AdminLayout>
    <div class="admin-page products-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><ShoppingCart /></el-icon> 商品管理</h1>
          <p>管理积分商城商品</p>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增商品
        </el-button>
      </div>

      <div class="table-card">
        <el-table :data="products" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="商品信息" min-width="220">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image v-if="row.image" :src="getFullUrl(row.image)" class="product-img" fit="cover" />
                <div v-else class="no-image-box"><el-icon><Picture /></el-icon></div>
                <div class="product-info">
                  <span class="product-name">{{ row.name }}</span>
                  <el-tag :type="row.type === 1 ? 'primary' : 'success'" effect="dark" size="small" round>{{ row.type === 1 ? '虚拟' : '实物' }}</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="积分" width="100">
            <template #default="{ row }">
              <span class="points-badge">{{ row.pointsRequired }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="dark" round size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="handleEdit(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
              <el-button type="danger" text @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
        </div>
      </div>

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="620px" class="modern-dialog">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
          <el-form-item label="商品名称" prop="name"><el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit /></el-form-item>
          <el-form-item label="商品类型" prop="type"><el-radio-group v-model="form.type"><el-radio :value="1">虚拟商品</el-radio><el-radio :value="2">实体商品</el-radio></el-radio-group></el-form-item>
          <el-form-item label="商品图片">
            <el-upload class="image-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleImageSuccess" :before-upload="beforeImageUpload">
              <img v-if="form.image" :src="getFullUrl(form.image)" class="product-image" />
              <div v-else class="image-placeholder"><el-icon :size="32"><Plus /></el-icon><span>上传图片</span></div>
            </el-upload>
          </el-form-item>
          <el-form-item label="商品描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" maxlength="500" show-word-limit /></el-form-item>
          <el-row :gutter="20">
            <el-col :span="12"><el-form-item label="所需积分" prop="pointsRequired"><el-input-number v-model="form.pointsRequired" :min="1" :max="99999" style="width: 100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="库存" prop="stock"><el-input-number v-model="form.stock" :min="0" :max="99999" style="width: 100%" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">上架</el-radio><el-radio :value="0">下架</el-radio></el-radio-group></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="submitting">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, ShoppingCart, Edit, Delete, Picture } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminGoodsList, createGoods, updateGoods, deleteGoods } from '@/api/admin'

interface ProductForm { id?: number; name: string; type: number; image: string; description: string; pointsRequired: number; stock: number; sort: number; status: number }

const loading = ref(false); const submitting = ref(false); const dialogVisible = ref(false); const isEdit = ref(false); const products = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0); const formRef = ref<FormInstance>()
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const form = reactive<ProductForm>({ name: '', type: 1, image: '', description: '', pointsRequired: 100, stock: 100, sort: 0, status: 1 })
const formRules: FormRules = { name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }], type: [{ required: true, message: '请选择商品类型', trigger: 'change' }], pointsRequired: [{ required: true, message: '请输入所需积分', trigger: 'blur' }], stock: [{ required: true, message: '请输入库存', trigger: 'blur' }] }

const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return `${import.meta.env.VITE_API_BASE_URL}${url}` }
const beforeImageUpload: UploadProps['beforeUpload'] = (file) => { if (!file.type.startsWith('image/')) { ElMessage.error('只能上传图片格式!'); return false }; if (file.size / 1024 / 1024 > 2) { ElMessage.error('图片大小不能超过 2MB!'); return false }; return true }
const handleImageSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) { form.image = response.data || response.message; ElMessage.success('图片上传成功') } else { ElMessage.error(response.message || '图片上传失败') } }

const fetchProducts = async () => { loading.value = true; try { const res = await getAdminGoodsList({ current: page.value, size: pageSize.value }); if (res.data) { products.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取商品列表失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchProducts() }
const resetForm = () => { form.id = undefined; form.name = ''; form.type = 1; form.image = ''; form.description = ''; form.pointsRequired = 100; form.stock = 100; form.sort = 0; form.status = 1 }
const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; form.id = row.id; form.name = row.name; form.type = row.type; form.image = row.image || ''; form.description = row.description || ''; form.pointsRequired = row.pointsRequired; form.stock = row.stock; form.sort = row.sort || 0; form.status = row.status; dialogVisible.value = true }
const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (valid) { submitting.value = true; try { if (isEdit.value) { await updateGoods(form); ElMessage.success('更新成功') } else { await createGoods(form); ElMessage.success('创建成功') }; dialogVisible.value = false; fetchProducts() } catch (error: any) { ElMessage.error(error.message || '保存失败') } finally { submitting.value = false } } }) }
const handleDelete = (row: any) => { ElMessageBox.confirm(`确认删除商品《${row.name}》吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await deleteGoods(row.id); ElMessage.success('删除成功'); fetchProducts() } catch (error: any) { ElMessage.error(error.message || '删除失败') } }) }

onMounted(() => fetchProducts())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;
    .header-left {
      h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      p { color: #6b7280; margin: 0; }
    }
  }

  .table-card {
    background: white; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .product-cell {
      display: flex; align-items: center; gap: 12px;
      .product-img { width: 60px; height: 60px; border-radius: 8px; }
      .no-image-box { width: 60px; height: 60px; background: #f3f4f6; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #d1d5db; }
      .product-info { .product-name { display: block; font-weight: 500; margin-bottom: 4px; } }
    }

    .points-badge { background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%); color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; }
    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}

.image-uploader {
  .product-image { width: 120px; height: 120px; object-fit: cover; border-radius: 8px; }
  .image-placeholder {
    width: 120px; height: 120px; border: 2px dashed #e5e7eb; border-radius: 8px;
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
    color: #9ca3af; cursor: pointer; transition: all 0.2s;
    &:hover { border-color: #D64541; color: #D64541; }
  }
}
</style>
