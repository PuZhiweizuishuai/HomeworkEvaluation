<template>
  <v-card outlined>
    <v-navigation-drawer
      floating
      permanent
    >
      <v-list rounded>
        <router-link v-for="(item, index) in items" :key="item.id" :to="`/course/tag/${item.id}`">
          <v-list-item
            v-if="index < length"
            link
          >
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ item.courseMajor }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </router-link>
      </v-list>
    </v-navigation-drawer>
    <v-menu open-on-hover top offset-y>
      <template v-slot:activator="{ on, attrs }">
        <v-list>
          <v-list-item
            v-bind="attrs"
            link
            v-on="on"
          >
            <v-list-item-icon>
              <v-icon />
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>更多</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </template>
      <v-list rounded>
        <router-link v-for="(item, index) in items" :key="item.id" :to="`/course/tag/${item.id}`">
          <v-list-item
            v-if="index >= length"
            link
          >
            <v-list-item-title>{{ item.courseMajor }}</v-list-item-title>
          </v-list-item>
        </router-link>
      </v-list>
    </v-menu>
  </v-card>
</template>

<script>

/**
 * 主页显示课程分类
 */
export default {
  name: 'CourseList',
  props: {
    items: {
      type: Array,
      default: () => []
    },
    length: {
      type: Number,
      default: 6
    }
  },
  data() {
    return {

    }
  },
  created() {

  }
}
</script>

<style>

</style>
