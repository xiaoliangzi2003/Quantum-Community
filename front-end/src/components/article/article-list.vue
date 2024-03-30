<template>
  <div>
    <el-menu mode="horizontal" class="sort-menu">
      <div class="menu-items">
        <el-menu-item index="1">默认排序</el-menu-item>
        <el-menu-item index="2">最新排序</el-menu-item>
      </div>
      <el-input v-model="search" placeholder="搜索文章" class="menu-search"></el-input>
    </el-menu>
  <div class="articles" @scroll.passive="handleScroll">
    <el-card class="article" v-for="article in articles" :key="article.id" @click="goToArticleDetail(article)">
      <h2 class="article-title">{{ article.title }}</h2>
      <p class="article-summary">{{ article.summary }}</p>
      <div class="article-stats">
        <div class="icon-stat">
          <el-tooltip content="浏览量">
            <img src="../../../public/image/icon/view.svg" alt="view icon" class="input-icon"/>
          </el-tooltip>
          <span>{{ article.views }}</span>
        </div>
        <div class="icon-stat">
          <el-tooltip content="点赞量">
            <img src="../../../public/image/icon/like_disable.svg" alt="thumb icon" class="input-icon"/>
          </el-tooltip>
          <span>{{ article.likes }}</span>
        </div>
        <div class="icon-stat">
          <el-tooltip content="评论量">
            <img src="../../../public/image/icon/comment.svg" alt="comment icon" class="input-icon"/>
          </el-tooltip>
          <span>{{ article.comments }}</span>
        </div>
        <div class="icon-stat">
          <el-tooltip content="收藏量">
            <img src="../../../public/image/icon/collect_disable.svg" alt="star icon" class="input-icon"/>
          </el-tooltip>
          <span>{{ article.collects }}</span>
        </div>
        <div class="icon-stat">
          <el-tooltip content="转发量">
            <img src="../../../public/image/icon/share.svg" alt="star icon" class="input-icon"/>
          </el-tooltip>
          <span>{{ article.forwards}}</span>
        </div>
      </div>
      <div class="article-info">
        <p class="article-author">作者: {{ article.author }}</p>
        <p class="article-category">分类: {{ article.category }}</p>
        <div class="article-tags">
          <el-tag class="article-tag" v-for="tag in article.tags.split(',')" :key="tag">{{ tag }}</el-tag>
        </div>
      </div>
    </el-card>
    <div v-if="hasMore" @click="loadMoreArticles" class="load-more">点击加载更多</div>
  </div>
  </div>
</template>

<script>

import axios from "axios";

export default {
  name: 'Articles',
  data() {
    return {
      articles: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      hasMore: true,
      authorAvatar: '',
      search: '',
    }
  },
  methods: {
    goToArticleDetail(article) {
      this.$router.push(`/article-detail/${article.id}`);
    },
    loadMoreArticles() {
      if (this.loading) return; // If already loading, do nothing
      this.loading = true;
      let url= "http://localhost:8080/blog/article-list";
      let data={
        pageNum:this.pageNum,
        pageSize:this.pageSize
      }
      axios.post(url, data)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.articles = this.articles.concat(response.data.data.articleList);
              this.pageNum++;
              if (response.data.data.articleList.length < this.pageSize) {
                this.hasMore = false;
              }

              this.articles.forEach(article=> {
                article.authorAvatar = `http://localhost:8080/user-profile/get-avatar/${article.author}`;
              })
            } else {
              console.error('Failed to load articles: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Failed to load articles: ' + error);
          })
          .finally(() => {
            this.loading = false;
          });
    },
    handleScroll(event) {
      const { target } = event;
      if (Math.ceil(target.scrollTop) + target.clientHeight >= target.scrollHeight) {
        this.loadMoreArticles();
      }
    }
  },
  mounted() {
    this.loadMoreArticles();
  }
}
</script>

<style scoped>
.menu-search {
  width: 300px;
  margin-top: 10px;
  margin-bottom: 10px;
  margin-right: 20px;
}

.menu-items {
  display: flex;
}


.sort-menu{
  border-radius: 5px;
  display: flex;
  justify-content: space-between;
}
.article-author,
.article-category {
  display: block;  /* 修改为 block 或其他合适的值 */
}

.author-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 20px;
  text-align: right;
}

.article-tag{
  margin-right: 10px;
}
.load-more {
  text-align: center;
  padding: 10px;
  cursor: pointer;
}

.icon-stat {
  display: flex;
  align-items: center;
}

.input-icon {
  width: 20px;
  height: 20px;
  padding: 0 10px;
}

.article-title {
  text-align:left;
  margin-top: 0;
  margin-left: 20px;
}

.article-summary{
  margin-left: 20px;
}


.article-info{
  text-align: right;

}
.article-date {
  position: absolute;
  top: 10px;
  right: 10px;
  margin-right: 20px;
}

.article-stats {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 10px;
  left: 10px;

}

.article-author,
.article-category {
  display: none;  /* 隐藏作者和分类 */
}

.article-tags {

  margin-top:20px;
}

.index-container {
  height: 100vh;
}

.articles {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  overflow-y: scroll; /* Allow vertical scrolling */
}

.articles::-webkit-scrollbar {
  display: none; /* Hide the scrollbar */
}

.article {
  width: 100%;
  margin-bottom: 5px;
  position: relative;
  padding-top: 0;
  box-sizing: border-box;
}

.index-top-navigation {
  background-color: black;
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: black;
}

.search-input {
  color: white;
  background-color: black;
  font-size: 16px;
}
</style>