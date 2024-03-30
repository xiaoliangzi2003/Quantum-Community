<template>
  <div>
    <el-menu mode="horizontal" class="sort-menu">
      <div class="menu-items">
        <el-menu-item index="1">默认排序</el-menu-item>
        <el-menu-item index="2">最新排序</el-menu-item>
      </div>
      <el-input v-model="search" placeholder="搜索问题" class="menu-search"></el-input>
    </el-menu>
    <div class="questions" @scroll.passive="handleScroll">
      <el-card class="question" v-for="question in questions" :key="question.questionId" @click="goToQuestionDetail(question)">
        <h2 class="question-title">{{ question.questionTitle}}</h2>
        <div class="question-stats">
          <div class="icon-stat">
            <el-tooltip content="浏览量">
              <img src="../../../public/image/icon/view.svg" alt="view icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ question.views }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="点赞量">
              <img src="../../../public/image/icon/like_disable.svg" alt="thumb icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ question.likes }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="收藏量">
              <img src="../../../public/image/icon/collect_disable.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ question.collects }}</span>
          </div>
          <div class="icon-stat">
            <el-tooltip content="转发量">
              <img src="../../../public/image/icon/share.svg" alt="star icon" class="input-icon"/>
            </el-tooltip>
            <span>{{ question.forwards}}</span>
          </div>
        </div>
        <div class="question-info">
          <p class="question-category">分类: {{ question.category }}</p>
          <div class="question-tags">
            <el-tag class="question-tag" v-for="tag in question.tags.split(',')" :key="tag">{{ tag }}</el-tag>
          </div>
        </div>
      </el-card>
      <div v-if="hasMore" @click="loadMoreQuestions" class="load-more">点击加载更多</div>
    </div>
  </div>
</template>

<script>

import axios from "axios";

export default {
  name: 'questions',
  data() {
    return {
      questions: [],
      pageNum: 1,
      pageSize: 10,
      loading: false,
      hasMore: true,
      authorAvatar: '',
      search: '',
    }
  },
  methods: {
    goToQuestionDetail(question) {
      this.$router.push(`/question-detail/${question.questionId}`);
    },
    loadMoreQuestions() {
      if (this.loading) return; // If already loading, do nothing
      this.loading = true;
      let url= "http://localhost:8080/question/question-list";
      let data={
        pageNum:this.pageNum,
        pageSize:this.pageSize
      }
      axios.post(url, data)
          .then(response => {
            if (response.data.statusCode === 200) {
              this.questions = this.questions.concat(response.data.data);
              this.pageNum++;
              if (response.data.data.length < this.pageSize) {
                this.hasMore = false;
              }
            } else {
              console.error('Failed to load questions: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Failed to load questions: ' + error);
          })
          .finally(() => {
            this.loading = false;
          });
    },
    handleScroll(event) {
      const { target } = event;
      if (Math.ceil(target.scrollTop) + target.clientHeight >= target.scrollHeight) {
        this.loadMoreQuestions();
      }
    }
  },
  mounted() {
    this.loadMoreQuestions();
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
.question-author,
.question-category {
  display: block;  /* 修改为 block 或其他合适的值 */
}

.author-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 20px;
  text-align: right;
}

.question-tag{
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

.question-title {
  text-align:left;
  margin-top: 0;
  margin-left: 20px;
}

.question-summary{
  margin-left: 20px;
}


.question-info{
  text-align: right;

}
.question-date {
  position: absolute;
  top: 10px;
  right: 10px;
  margin-right: 20px;
}

.question-stats {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 10px;
  left: 10px;

}

.question-author,
.question-category {
  display: none;  /* 隐藏作者和分类 */
}

.question-tags {

  margin-top:20px;
}

.index-container {
  height: 100vh;
}

.questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  overflow-y: scroll; /* Allow vertical scrolling */
}

.questions::-webkit-scrollbar {
  display: none; /* Hide the scrollbar */
}

.question {
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