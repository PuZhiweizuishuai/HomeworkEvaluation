const status = [
  '作业未发布，需要等待添加题目后发布！',
  '作业未开始',
  '作业开始',
  '评价开始',
  '评价完成',
  '作业结束'
]

const type = [
  '普通作业，不限时间',
  '限时测验',
  '考试'
]

export function getHomeworkStatus(code) {
  if (code > 4 || code < 0) {
    return '未知或状态输入错误！'
  }
  return status[code + 1]
}

export function getHomeworkType(code) {
  if (code > 2 || code < 0) {
    return '未知或类型输入错误！'
  }
  return type[code]
}
