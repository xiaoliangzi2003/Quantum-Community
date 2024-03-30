<template>
  <div class="user-manage">
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" :cell-class-name="columnClassName"></el-table-column>
      <el-table-column prop="username" label="用户名" :cell-class-name="columnClassName"></el-table-column>
      <el-table-column prop="onlineStatus" label="在线状态" :cell-class-name="columnClassName"></el-table-column>
      <el-table-column prop="gender" label="性别" :cell-class-name="columnClassName"></el-table-column>
      <el-table-column prop="address" label="住址" :cell-class-name="columnClassName"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'UserManage',
  data() {
    return {
      users: Array.from({ length: 50 }, (_, i) => ({
        id: i + 1,
        username: `user${i + 1}`,
        onlineStatus: i % 2 === 0 ? '在线' : '离线',
        gender: i % 2 === 0 ? '男' : '女',
        address: `地址${i + 1}`
      }))
    }
  },
  methods: {
    columnClassName({ columnIndex }) {
      return `color${columnIndex % 5 + 1}`;
    }
  }
}
</script>

<style scoped>
.user-manage {
  padding: 20px;
}
.color1 {
  color: red !important;
}

.color2 {
  color: orange !important;
}

.color3 {
  color: yellow !important;
}

.color4 {
  color: green !important;
}

.color5 {
  color: blue !important;
}
</style>