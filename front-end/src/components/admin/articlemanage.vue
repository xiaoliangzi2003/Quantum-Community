<template>
  <div class="article-manage">
    <el-dialog :visible.sync="dialogVisible" title="编辑文章">
      <!-- 在这里添加表单元素，例如： -->
      <el-form ref="form" :model="currentArticle" label-width="120px">
        <el-form-item label="文章状态">
          <el-select v-model="currentArticle.status" placeholder="请选择">
            <el-option label="发布" value="publish"></el-option>
            <el-option label="草稿" value="draft"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-switch v-model="currentArticle.visibility"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateArticle">确 定</el-button>
      </div>
    </el-dialog>
    <el-table :data="articles" style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="title" label="标题"></el-table-column>
      <el-table-column prop="author" label="作者"></el-table-column>
      <el-table-column prop="createTimeStamp" label="创建时间" :formatter="formatDate"></el-table-column>
      <el-table-column prop="views" label="浏览量"></el-table-column>
      <el-table-column prop="likes" label="点赞量"></el-table-column>
      <el-table-column prop="comments" label="评论量"></el-table-column>
      <el-table-column prop="collects" label="收藏量"></el-table-column>
      <el-table-column prop="forwards" label="转发量"></el-table-column>
      <el-table-column prop="tags" label="标签"></el-table-column>
      <el-table-column prop="status" label="状态"></el-table-column>
      <el-table-column prop="category" label="分类"></el-table-column>
      <el-table-column prop="visibility" label="可见性"></el-table-column>
      <el-table-column label="操作">
        <template v-slot:default="scope">
          <el-button icon="el-icon-edit" @click="openDialog(scope.row)"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        @current-change="handlePageChange"
        :current-page="currentPage"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalArticles">
    </el-pagination>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ArticleManage',
  data() {
    return {
      articles: [],
      currentPage: 1,
      pageSize: 10,
      totalArticles: 0,
      dialogVisible: false,
      currentArticle: null
    }
  },

  created(){
    this.fetchArticles();
  },

  methods:{
    fetchArticles(){
      axios.post('http://localhost:8080/blog/article-list',{
        pageNum:this.currentPage,
        pageSize:this.pageSize
      }).then(response=>{
        if(response.data.statusCode===200){
          this.articles = response.data.data.articleList;
          this.totalArticles = response.data.data.total;
        }else{
          console.log('Failed to fetch articles: '+response.data.message);
        }
      }).catch(error=>{
        console.error('Failed to fetch articles: '+error);
      })
    },
    handlePageChange(newPage){
      this.currentPage = newPage;
      this.fetchArticles();
    },
    formatDate(row, column) {
      let date = new Date(row[column.property]);
      return date.toLocaleString();
    },
    openDialog(article) {
      this.currentArticle = Object.assign({}, article); // 创建文章的副本
      this.dialogVisible = true;
      this.$forceUpdate();
    },
    updateArticle() {
      axios.post(`http://localhost:8080/blog/article-update/${this.currentArticle.id}`, this.currentArticle)
          .then(response => {
            if(response.data.statusCode === 200){
              // 更新成功，关闭对话框并刷新文章列表
              this.dialogVisible = false;
              this.fetchArticles();
            }else{
              console.log('Failed to update article: '+response.data.message);
            }
          }).catch(error=>{
        console.error('Failed to update article: '+error);
      })
    }
  }
}
</script>

<style scoped>
.article-manage {
  padding: 20px;
}
</style>