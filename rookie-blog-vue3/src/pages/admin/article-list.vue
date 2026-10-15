<template>
  <div>
    <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
    <el-card shadow="never" class="mb-5">
      <!-- flex 布局，内容垂直居中 -->
      <div class="flex items-center">
        <el-text>文章标题</el-text>
        <div class="ml-3 w-52 mr-5">
          <el-input v-model="searchArticleTitle" placeholder="请输入（模糊查询）" />
        </div>
        <el-text>创建时间</el-text>
        <div class="ml-3 w-30 mr-5">
          <!-- 日期选择组件（区间选择） -->
          <el-date-picker
            v-model="pickDate"
            type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            :shortcuts="shortcuts"
            size="default"
            @change="datepickerChange"
          />
        </div>
        <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
        <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
      </div>
    </el-card>
    <el-card shadow="never">
      <!-- 写文章按钮 -->
      <div class="mb-5">
        <el-button type="primary" @click="isArticlePublishEditorShow = true">
          <el-icon class="mr-1">
            <Plus />
          </el-icon>
          写文章</el-button
        >
      </div>
      <!-- 写博客 -->
      <el-dialog
        v-model="isArticlePublishEditorShow"
        :fullscreen="true"
        :show-close="false"
        :close-on-press-escape="false"
        :close-on-click-modal="false"
      >
        <template #header="{ close, titleId, titleClass }">
          <!-- 固钉组件，固钉到顶部 -->
          <el-affix :offset="20" style="width: 100%">
            <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
            <div class="flex h-10">
              <!-- 字体加粗 -->
              <h4 class="font-bold text-white">写文章</h4>
              <!-- 靠右对齐 -->
              <div class="ml-auto flex">
                <el-button @click="closeEditor('publish')">取消</el-button>
                <el-button
                  :loading="publishing"
                  :disabled="publishing"
                  @click="publishArticleSubmit(0)"
                >
                  <el-icon class="mr-1">
                    <Document />
                  </el-icon>
                  存草稿
                </el-button>
                <el-button
                  type="primary"
                  :loading="publishing"
                  :disabled="publishing"
                  @click="publishArticleSubmit(1)"
                >
                  <el-icon class="mr-1">
                    <Promotion />
                  </el-icon>
                  发布
                </el-button>
              </div>
            </div>
          </el-affix>
        </template>
        <el-form
          :model="form"
          ref="publishArticleFormRef"
          label-position="top"
          size="large"
          :rules="rules"
          novalidate
        >
          <p v-if="submitError" role="alert" class="text-red-400 mb-4">{{ submitError }}</p>
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="form.title"
              autocomplete="off"
              size="large"
              maxlength="150"
              show-word-limit
              clearable
            />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <!-- Markdown 编辑器 -->
            <MdEditor
              v-model="form.content"
              @onUploadImg="onUploadImg"
              editorId="publishArticleEditor"
              theme="dark"
            />
          </el-form-item>
          <el-form-item label="封面（可选）" prop="cover">
            <el-upload
              class="avatar-uploader"
              action="#"
              :on-change="handleCoverChange"
              :auto-upload="false"
              :show-file-list="false"
            >
              <img v-if="form.cover" :src="form.cover" class="avator" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="摘要" prop="summary">
            <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
            <el-input
              v-model="form.summary"
              type="textarea"
              rows="4"
              maxlength="200"
              placeholder="请输入文章摘要"
            />
          </el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large">
              <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标签" prop="tagIds">
            <el-select
              v-model="form.tagIds"
              placeholder="请选择标签"
              clearable
              size="large"
              multiple
              filterable
              remote
              reserve-keyword
              remote-show-suffix
              :remote-method="remoteMethod"
              allow-create
              default-first-option
              :loading="tagSelectLoading"
            >
              <el-option
                v-for="item in tags"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="置顶">
            <el-switch v-model="form.isTop" active-text="置顶该文章" />
          </el-form-item>
        </el-form>
      </el-dialog>
      <!-- 编辑文章对话框 -->
      <el-dialog
        v-model="isArticleUpdateEditorShow"
        :fullscreen="true"
        :show-close="false"
        :close-on-press-escape="false"
      >
        <template #header="{ close, titleId, titleClass }">
          <!-- 固钉组件，固钉到顶部 -->
          <el-affix :offset="20" style="width: 100%">
            <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
            <div class="flex h-10">
              <h4 class="font-bold text-white">编辑文章</h4>
              <div class="ml-auto flex">
                <el-button @click="closeEditor('update')">取消</el-button>
                <el-button type="primary" :loading="updating" @click="updateSubmit">
                  <el-icon class="mr-1">
                    <Promotion />
                  </el-icon>
                  保存
                </el-button>
              </div>
            </div>
          </el-affix>
        </template>
        <el-form
          :model="updateArticleForm"
          ref="updateArticleFormRef"
          label-position="top"
          size="large"
          :rules="rules"
          novalidate
        >
          <p v-if="submitError" role="alert" class="text-red-400 mb-4">{{ submitError }}</p>
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="updateArticleForm.title"
              autocomplete="off"
              size="large"
              maxlength="150"
              show-word-limit
              clearable
            />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <!-- Markdown 编辑器 -->
            <MdEditor
              v-model="updateArticleForm.content"
              @onUploadImg="onUploadImg"
              editorId="updateArticleEditor"
              theme="dark"
            />
          </el-form-item>
          <el-form-item label="封面（可选）" prop="cover">
            <el-upload
              class="avatar-uploader"
              action="#"
              :on-change="handleUpdateCoverChange"
              :auto-upload="false"
              :show-file-list="false"
            >
              <img v-if="updateArticleForm.cover" :src="updateArticleForm.cover" class="avator" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="摘要" prop="summary">
            <el-input
              v-model="updateArticleForm.summary"
              type="textarea"
              rows="4"
              maxlength="200"
              placeholder="请输入文章摘要"
            />
          </el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select
              v-model="updateArticleForm.categoryId"
              clearable
              placeholder="---请选择---"
              size="large"
            >
              <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标签" prop="tags">
            <span class="w-60">
              <!-- 标签选择 -->
              <el-select
                v-model="updateArticleForm.tags"
                multiple
                filterable
                remote
                reserve-keyword
                placeholder="请输入文章标签"
                remote-show-suffix
                allow-create
                default-first-option
                :remote-method="remoteMethod"
                :loading="tagSelectLoading"
                size="large"
              >
                <el-option
                  v-for="item in tags"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </span>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="updateArticleForm.status">
              <el-radio :label="1">已发布</el-radio>
              <el-radio :label="0">草稿</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="置顶">
            <el-switch v-model="updateArticleForm.isTop" active-text="置顶该文章" />
          </el-form-item>
        </el-form>
      </el-dialog>
      <!-- 分页列表 -->
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
        <el-table-column prop="title" label="标题" min-width="260" />
        <el-table-column prop="cover" label="封面（可选）" width="120">
          <template #default="scope">
            <span v-if="!scope.row.cover" class="text-gray-400">无封面</span
            ><el-image v-else :src="scope.row.cover" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="200" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
            <el-tag v-else type="info">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.isTop" type="warning">置顶</el-tag>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="showArticleUpdateEditor(scope.row)">
              <el-icon class="mr-1">
                <Edit />
              </el-icon>
              编辑</el-button
            >
            <el-button type="danger" size="small" @click="deleteArticleSubmit(scope.row)">
              <el-icon class="mr-1">
                <Delete />
              </el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="mt-10 flex justify-center">
        <el-pagination
          v-model:current-page="current"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50]"
          :small="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="getTableData"
        />
      </div>
    </el-card>
  </div>
</template>
<script setup>
// 引入所需图标
import { Search, RefreshRight, Document } from '@element-plus/icons-vue'
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import {
  getArticlePageList,
  deleteArticle,
  publishArticle,
  getArticleDetail,
  updateArticle
} from '@/api/admin/article'
import { onBeforeRouteLeave } from 'vue-router'
import { showMessage, showModel } from '@/composables/util'
import moment from 'moment'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { uploadFile } from '@/api/admin/file'
import { getCategorySelectList } from '@/api/admin/category'
import { searchTags, getTagSelectList } from '@/api/admin/tag'

// 模糊搜索文章标题
const searchArticleTitle = ref('')
// 日期
const pickDate = ref('')
// 查询条件：开始结束时间
const startDate = reactive({})
const endDate = reactive({})
// 监听日期组件改变事件，并将开始结束时间设置到变量中
const datepickerChange = (e) => {
  startDate.value = moment(e[0]).format('YYYY-MM-DD')
  endDate.value = moment(e[1]).format('YYYY-MM-DD')

  console.log('开始时间：' + startDate.value + ', 结束时间：' + endDate.value)
}
const shortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  }
]
// 重置
const reset = () => {
  pickDate.value = ''
  startDate.value = null
  endDate.value = null
  searchArticleTitle.value = ''
}
// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
//当前页码，给一个默认值1
const current = ref(1)
//每页显示条数
const size = ref(10)
//总条数
const total = ref(0)

// 获取表格数据
function getTableData() {
  // 开启表格加载 Loading
  tableLoading.value = true
  // 调用接口获取数据
  getArticlePageList({
    current: current.value,
    size: size.value,
    startDate: startDate.value,
    endDate: endDate.value,
    title: searchArticleTitle.value
  })
    .then((res) => {
      if (res.success == true) {
        tableData.value = res.data
        current.value = res.current
        size.value = res.size
        total.value = res.total
      }
    })
    .finally(() => {
      // 关闭表格加载 Loading
      tableLoading.value = false
    })
}
getTableData()

// 分页组件的页码改变事件
const handleSizeChange = (chooseSize) => {
  size.value = chooseSize
  getTableData()
}
const deleteArticleSubmit = (row) => {
  showModel('是否确认删除该文章？')
    .then(() => {
      deleteArticle(row.id).then((res) => {
        if (res.success == false) {
          // 获取服务端返回的错误消息
          let message = res.message
          // 提示错误消息
          showMessage(message, 'error')
          return
        }
        showMessage('删除成功')
        // 重新请求分页接口，渲染数据
        getTableData()
      })
    })
    .catch(() => {
      showMessage('取消删除')
    })
}

//是否显示文章发布对话框
const isArticlePublishEditorShow = ref(false)

// 文章发布表单引用
const publishArticleFormRef = ref(null)
//表单对象
const form = reactive({
  id: null,
  title: '',
  content: '',
  cover: '',
  summary: '',
  categoryId: null,
  tagIds: [],
  status: 1,
  isTop: false
})
//表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 150, message: '长度在 1 到 150 个字符', trigger: 'blur' }
  ],
  content: [{ required: true }],
  categoryId: [{ required: true, message: '请选择文章分类', trigger: 'blur' }],
  tags: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
  tagIds: [{ required: true, message: '请选择文章标签', trigger: 'blur' }]
}
//上传封面图片
const handleCoverChange = (file) => {
  //表单对象
  let formData = new FormData()
  //添加file字段，并将文件传入
  formData.append('file', file.raw)
  uploadFile(formData).then((e) => {
    //响应失败，提示错误信息
    if (e.success == false) {
      let message = e.message
      showMessage(message, 'error')
      return
    }
    //成功则设置表单对象中的中的封面链接，并提示上传成功
    form.cover = e.data.url
    showMessage('上传成功')
  })
}

//编辑器图片上传
const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
    files.map((file) => {
      return new Promise((rev, rej) => {
        console.log('==》编辑器开始上传文件。。。')
        let formData = new FormData()
        formData.append('file', file)
        uploadFile(formData).then((res) => {
          console.log('访问路径：' + res.data.url)
          // 调用 callback 函数，回显上传图片
          callback([res.data.url])
        })
      })
    })
  )
}

// 文章分类
const categories = ref([])
getCategorySelectList().then((e) => {
  console.log('获取分类数据')
  categories.value = e.data
})

// 标签 select Loading 状态，默认不显示
const tagSelectLoading = ref(false)
// 文章标签
const tags = ref([])
//根据用户输入的标签名称，远程模糊查询
const remoteMethod = (query) => {
  console.log('远程检索：' + tags.value)
  //如果用户查询的关键词不为空
  if (query) {
    //显示loading
    tagSelectLoading.value = true
    //调用标签模糊查询接口
    searchTags(query)
      .then((e) => {
        if (e.success) {
          //设置到tags变量中
          tags.value = e.data
        }
      })
      .finally(() => (tagSelectLoading.value = false))
  }
}

//发布文章（status: 1-发布, 0-存草稿）
const publishing = ref(false),
  updating = ref(false),
  submitError = ref('')
const publishArticleSubmit = async (status) => {
  if (publishing.value || !(await publishArticleFormRef.value.validate().catch(() => false))) return
  publishing.value = true
  submitError.value = ''
  try {
    const res = await publishArticle({ ...form, status })
    if (!res.success) throw new Error(res.message)
    showMessage(status === 0 ? '草稿已保存' : '文章已发布')
    isArticlePublishEditorShow.value = false
    Object.assign(form, {
      id: null,
      title: '',
      content: '',
      cover: '',
      summary: '',
      categoryId: null,
      tagIds: [],
      status: 1,
      isTop: false
    })
    getTableData()
  } catch (e) {
    submitError.value = e.message || '保存失败，编辑内容仍保留'
  } finally {
    publishing.value = false
  }
}

//是否显示文章编辑对话框
const isArticleUpdateEditorShow = ref(false)
const updateArticleFormRef = ref(null)
const updateArticleForm = reactive({
  id: null,
  title: '',
  content: '',
  cover: '',
  summary: '',
  categoryId: null,
  tags: [],
  status: 1,
  isTop: false
})
// 编辑文章：上传文章封面图片
const handleUpdateCoverChange = (file) => {
  // 表单对象
  let formData = new FormData()
  // 添加 file 字段，并将文件传入
  formData.append('file', file.raw)
  uploadFile(formData).then((e) => {
    // 响参失败，提示错误消息
    if (e.success == false) {
      let message = e.message
      showMessage(message, 'error')
      return
    }

    // 成功则设置表单对象中的封面链接，并提示上传成功
    updateArticleForm.cover = e.data.url
    showMessage('上传成功')
  })
}
// 编辑文章按钮点击事件
const showArticleUpdateEditor = async (row) => {
  submitError.value = ''
  try {
    const res = await getArticleDetail(row.id)
    if (!res.success) throw new Error(res.message)
    Object.assign(updateArticleForm, {
      ...res.data,
      cover: res.data.cover || '',
      tags: res.data.tagIds || []
    })
    isArticleUpdateEditorShow.value = true
  } catch (e) {
    showMessage(e.message || '文章加载失败，请重试', 'error')
  }
}
// 文章标签
// const tags = ref([])
// 渲染标签数据
getTagSelectList().then((res) => {
  tags.value = res.data
})

// 保存文章
const updateSubmit = async () => {
  if (updating.value || !(await updateArticleFormRef.value.validate().catch(() => false))) return
  updating.value = true
  submitError.value = ''
  try {
    const res = await updateArticle(updateArticleForm)
    if (!res.success) throw new Error(res.message)
    showMessage('保存成功')
    isArticleUpdateEditorShow.value = false
    getTableData()
  } catch (e) {
    submitError.value = e.message || '保存失败，编辑内容仍保留'
  } finally {
    updating.value = false
  }
}
const publishSnapshot = ref(''),
  updateSnapshot = ref('')
watch(isArticlePublishEditorShow, (open) => {
  if (open) {
    publishSnapshot.value = JSON.stringify(form)
    submitError.value = ''
  }
})
watch(isArticleUpdateEditorShow, (open) => {
  if (open) updateSnapshot.value = JSON.stringify(updateArticleForm)
})
const articleDirty = computed(
  () =>
    (isArticlePublishEditorShow.value && JSON.stringify(form) !== publishSnapshot.value) ||
    (isArticleUpdateEditorShow.value && JSON.stringify(updateArticleForm) !== updateSnapshot.value)
)
async function canLeaveEditor() {
  if (publishing.value || updating.value) return false
  if (!articleDirty.value) return true
  try {
    await showModel('有未保存的文章修改，离开会丢弃这些修改，是否继续？')
    return true
  } catch {
    return false
  }
}
function discardEditorChanges() {
  if (isArticlePublishEditorShow.value && publishSnapshot.value)
    Object.assign(form, JSON.parse(publishSnapshot.value))
  if (isArticleUpdateEditorShow.value && updateSnapshot.value)
    Object.assign(updateArticleForm, JSON.parse(updateSnapshot.value))
}
async function closeEditor(kind) {
  if (!(await canLeaveEditor())) return
  discardEditorChanges()
  if (kind === 'publish') isArticlePublishEditorShow.value = false
  else isArticleUpdateEditorShow.value = false
}
onBeforeRouteLeave(async () => {
  if (!(await canLeaveEditor())) return false
  discardEditorChanges()
  isArticlePublishEditorShow.value = false
  isArticleUpdateEditorShow.value = false
  return true
})
function beforeUnload(event) {
  if (articleDirty.value) {
    event.preventDefault()
    event.returnValue = ''
  }
}
onMounted(() => window.addEventListener('beforeunload', beforeUnload))
onBeforeUnmount(() => window.removeEventListener('beforeunload', beforeUnload))
</script>
<style scoped>
/* 封面图片样式 */
.avatar-uploader .avatar {
  width: 200px;
  height: 100px;
  display: block;
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  text-align: center;
}
</style>
<style>
.md-editor-footer {
  height: 40px;
}
/* 指定 select 下拉框宽度 */
.el-select--large {
  width: 600px;
}
</style>
