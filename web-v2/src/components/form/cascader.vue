<template>
  <v-row justify="center">
    <v-col cols="5">
      <v-select
        :items="father"
        :label="fatherHint"

        @change="getFatherCategory"
      />
    </v-col>
    <v-col cols="5">
      <v-select
        :items="children"
        :label="hint"

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
    },
    select: {
      type: Boolean,
      default: false
    },
    values: {
      type: Array,
      default: () => []
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
      tag: [],
      fatherHint: '',
      hint: ''
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
      this.fatherHint = this.label
      this.hint = this.label
      for (let i = 0; i < this.items.length; i++) {
        const name = this.items[i].courseMajor
        this.categoryMap.Set(name, this.items[i])
        this.father.push(name)
        if (this.select) {
          if (this.values[0] === 0) {
            this.values[0] = this.$store.state.editCourseTag[0]
          }
          if (this.values[1] === 0) {
            this.values[1] = this.$store.state.editCourseTag[1]
          }
          if (this.items[i].id === this.values[0]) {
            this.fatherHint = name
            const c = this.items[i].children
            for (let i = 0; i < c.length; i++) {
              if (c[i].id === this.values[1]) {
                this.hint = c[i].courseMajor
              }
            }
          }
        }
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
