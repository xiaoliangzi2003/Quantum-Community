<template>
  <div class="article-detail">
    <div v-html="markdownContent" class="markdown-body"></div>
  </div>
</template>

<script>
import axios from 'axios'
import * as marked from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

export default {
  props: {
    articleId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      markdownContent: ''
    }
  },
  mounted() {
    this.fetchMarkdownContent()
  },
  methods: {
    fetchMarkdownContent() {
      axios.get(`http://localhost:8080/blog/article-detail/${this.$route.params.id}`)
          .then(response => {
            this.markdownContent = marked.parse(response.data.content, {
              highlight: function (code, language) {
                const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
                return hljs.highlight(validLanguage, code).value;
              }
            })
          })
          .catch(error => {
            console.error('Error fetching Markdown content:', error)
          })
    }
  }
}
</script>

<style>
/* 根据需要添加样式 */
.markdown-body {
  /* 样式可以自定义，也可以使用 highlight.js 提供的默认样式 */
}
</style>
