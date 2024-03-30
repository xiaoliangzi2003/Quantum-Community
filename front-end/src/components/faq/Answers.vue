<template>
  <div>
    <el-menu mode="horizontal" class="sort-menu">
      <div class="menu-items">
        <el-menu-item index="1">默认排序</el-menu-item>
        <el-menu-item index="2">最新排序</el-menu-item>
      </div>
      <el-input v-model="search" placeholder="搜索回答" class="menu-search"></el-input>
    </el-menu>
    <div class="answers" @scroll.passive="handleScroll">
      <el-card class="answer" v-for="answer in answers" :key="answer.id" @click="goToAnswerDetail(answer)">
        <h2 class="answer-title">{{ answer.title}}</h2>
        <div class="answer-stats">
          <div class="icon-stat">
            <el-tooltip content="浏览量">
              <img src="../../../public/image/icon/view.svg" alt="view icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ answer.views }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="点赞量">
              <img src="../../../public/image/icon/like_disable.svg" alt="thumb icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ answer.likes }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="收藏量">
              <img src="../../../public/image/icon/collect_disable.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ answer.collects }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="转发量">
              <img src="../../../public/image/icon/share.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ answer.forwards}}</span>
          </div>
        </div>
        <div class="answer-info">
          <p class="answer-category">分类: {{ answer.category }}</p>
          <div class="answer-tags">
            <el-tag class="answer-tag" v-for="tag in answer.tags.split(',')" :key="tag">{{ tag }}</el-tag>
          </div>
        </div>
      </el-card>
      <div v-if="hasMore" @click="loadMoreAnswers" class="load-more">点击加载更多</div>
    </div>
  </div>
</template>

<script>

import axios from "axios";

export default {
  name: 'answers',
  props: ['questionId'],
  data() {
    return {
      answers: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      hasMore: true,
      authorAvatar: '',
      search: '',
    }
  },
  methods: {
    goToAnswerDetail(answer) {
      this.$router.push(`/answer-detail/${answer.id}`);
    },
    loadMoreAnswers() {
      if (this.loading) return; // If already loading, do nothing
      this.loading = true;
      let url= "http://localhost:8080/question/answer-list";
      let data={
        pageNum:this.pageNum,
        pageSize:this.pageSize,
        questionId:parseInt(this.$route.params.id),
      }
      axios.post(url, data)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.answers = this.answers.concat(response.data.data);
              this.pageNum++;
              if (response.data.data.length < this.pageSize) {
                this.hasMore = false;
              }
            } else {
              console.error('Failed to load answers: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Failed to load answers: ' + error);
          })
          .finally(() => {
            this.loading = false;
          });
    },
    handleScroll(event) {
      const { target } = event;
      if (Math.ceil(target.scrollTop) + target.clientHeight >= target.scrollHeight) {
        this.loadMoreAnswers();
      }
    }
  },
  mounted() {
    this.loadMoreAnswers();
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
.answer-author,
.answer-category {
  display: block;  /* 修改为 block 或其他合适的值 */
}

.author-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 20px;
  text-align: right;
}

.answer-tag{
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

.answer-title {
  text-align:left;
  margin-top: 0;
  margin-left: 20px;
}

.answer-summary{
  margin-left: 20px;
}


.answer-info{
  text-align: right;

}
.answer-date {
  position: absolute;
  top: 10px;
  right: 10px;
  margin-right: 20px;
}

.answer-stats {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 10px;
  left: 10px;

}

.answer-author,
.answer-category {
  display: none;  /* 隐藏作者和分类 */
}

.answer-tags {

  margin-top:20px;
}

.index-container {
  height: 100vh;
}

.answers {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  overflow-y: scroll; /* Allow vertical scrolling */
}

.answers::-webkit-scrollbar {
  display: none; /* Hide the scrollbar */
}

.answer {
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