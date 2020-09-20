<template>
  <v-row justify="center">
    <v-col cols="5">
      <v-select
        :items="father"
        :label="label"
        @change="getFatherCategory"
      />
    </v-col>
    <v-col cols="5">
      <v-select
        :items="children"
        :label="label"
        @change="getChildrenCategory"
      />
    </v-col>
  </v-row>
</template>

<script>
/**
 * 级联选择
 */
export default {
  name: 'Cascader',
  props: {
    label: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      father: [],
      children: [],
      categoryMap: {
        Set: function(key, value) { this[key] = value },
        Get: function(key) { return this[key] },
        Contains: function(key) { return this.Get(key) != null },
        Remove: function(key) { delete this[key] }
      },
      nowCategory: {},
      items: [],
      tag: []
    }
  },
  created() {
    this.getTagList()
  },
  methods: {
    getTagList() {
      this.httpGet('/course/tag/list', (json) => {
        if (json.status === 200) {
          this.items = json.data
          this.initData()
        }
      })
    },
    initData() {
      this.father = []
      this.children = []
      for (let i = 0; i < this.items.length; i++) {
        const name = this.items[i].courseMajor
        this.categoryMap.Set(name, this.items[i])
        this.father.push(name)
      }
    },
    getFatherCategory(value) {
      this.children = []
      this.tag = []
      this.nowCategory = this.categoryMap.Get(value)
      this.tag.push(this.nowCategory.id)
      const c = this.nowCategory.children
      if (c) {
        for (let i = 0; i < c.length; i++) {
          this.children.push(c[i].courseMajor)
        }
      }
    },
    getChildrenCategory(value) {
      const c = this.nowCategory.children

      for (let i = 0; i < c.length; i++) {
        if (c[i].courseMajor === value) {
          this.tag.push(c[i].id)
          this.$emit('tag', this.tag)
        }
      }
    }
  }
}
</script>

<style>

</style>
