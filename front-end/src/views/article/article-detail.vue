<template>
  <el-container direction="vertical">
    <index-header></index-header>
    <el-container>
      <el-aside width="20%">
        <v-md-preview :source="markdownContent">
          <template #toc="{ toc }">
            <div class="toc">
              <div v-html="toc"></div>
            </div>
          </template>
        </v-md-preview>
      </el-aside>
      <el-main class="main-board">
        <article-card ref="articleCard"></article-card>
        <detail ref="detail" @contentChanged="renderToc"></detail>
      </el-main>
      <el-aside width="20%">
        <img
            v-if="isAuthor"
            class="delete-icon"
            src="../../../public/image/icon/delete.svg"
            @click="confirmDelete"
         alt=""/>
      </el-aside>
    </el-container>
    <el-footer>
      <CommentForm :articleId="$route.params.id" :commentatorId="commentatorId" class="comment-form"/>
    </el-footer>
  </el-container>
</template>

<script>
import Detail from '../../components/article/detail.vue';
import IndexHeader from '../../components/index/header.vue';
import ArticleCard from "../../components/article/article-card.vue";
import CommentForm from '../../components/article/comment.vue';
import Cookies from "js-cookie";
import { ElMessageBox } from 'element-plus'
import axios from 'axios';

export default {
  data() {
    return {
      markdownContent: '',
      commentatorId: Cookies.get('username') || '',
    }
  },
  components: {
    ArticleCard,
    Detail,
    IndexHeader,
    CommentForm,
  },
  methods: {
    confirmDelete() {
      ElMessageBox.confirm('确定要删除这篇文章吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteArticle();
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
      },
    deleteArticle() {
      axios.delete(`http://localhost:8080/blog/delete-article/${this.$route.params.id}`)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.$message.success('删除成功');
              this.$router.push('/index');
            } else {
              this.$message.error('删除失败');
            }
          })
          .catch(error => {
            console.log(error);
            this.$message.error('删除失败');
          });
    },
  }
};
</script>

<style scoped>

.main-board{
  display: flex;
  flex-direction: column;
}

.el-main {
  margin: 0 auto;
  width: 60% !important;
}

.el-aside {
  width: 20% !important;
}

.comment-form{
  margin-top: 20px;
}
</style>