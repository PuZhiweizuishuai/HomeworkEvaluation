<template>
  <div>
    <a-tree
      :selected-keys="selectedKeys"
      :tree-data="courseTagList"
    />
  </div>
</template>

<script>
export default {
  name: 'CourseTag',
  data() {
    return {
      courseTagList: [],
      expandedKeys: ['0-0-0', '0-0-1'],
      autoExpandParent: true,
      checkedKeys: ['0-0-0'],
      selectedKeys: []
    }
  },
  created() {
    this.getTreeList()
  },
  methods: {
    getTreeList() {
      fetch(this.SERVER_API_URL + '/course/tag/list', {
        headers: {
          'Content-Type': 'application/json; charset=UTF-8',
          'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
        },
        method: 'GET',
        credentials: 'include'
      }).then(response => response.json())
        .then(json => {
          this.courseTagList = this.getTreeData(json.data)
        })
        .catch(e => {

        })
    },
    getTreeData(data) {
      if (data === undefined) {
        return null
      }
      const result = data.map(o => {
        return {
          title: o.courseMajor,
          key: o.id,
          value: o.id,
          children: this.getTreeData(o.children)
        }
      })
      return result
    }
  }
}
</script>

