<template>
  <v-md-preview :text="markdownContent" ></v-md-preview>
</template>

<script>
import axios from 'axios';
import '@kangc/v-md-editor/lib/style/preview.css';
import '@kangc/v-md-editor/lib/theme/style/github.css';


export default {
  data() {
    return {
      markdownContent: ' ',
    };
  },
  mounted() {
    this.fetchMarkdownContent();
    this.updateArticleViews();
  },
  watch: {
    markdownContent(newContent) {
      this.$emit('contentChanged', newContent);
    }
  },
  methods: {
    fetchMarkdownContent() {
      axios.get(`http://localhost:8080/blog/article-detail/${this.$route.params.id}`)
          .then(response => {
            this.markdownContent = response.data.content;
            console.log(this.markdownContent);
          })
          .catch(error => {
            console.error('Error fetching Markdown content:', error);
          });
    },
    updateArticleViews() {
      axios.put(`http://localhost:8080/blog/update-article-views/${this.$route.params.id}`)
          .then(response => {
            if (response.data.status === 200) {
              console.log('Article views updated successfully');
            } else {
              console.error('Failed to update article views: ' + response.data.message);
            }
          })
          .catch(error => {
            console.error('Error updating article views:', error);
          });
    },
  },
};
</script>

<style scoped>

</style>
