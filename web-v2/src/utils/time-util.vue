<script>
function renderTime(date) {
  if (date === '' || date == null) {
    return ''
  }
  date = parseInt(date)
  const da = new Date(date)
  return da.getFullYear() + '-' + (da.getMonth() + 1) + '-' + da.getDate() + ' ' + da.getHours() + ':' + da.getMinutes() + ':' + da.getSeconds()
}

function formateTimeToChinese(date) {
  if (date === '' || date == null) {
    return ''
  }
  date = parseInt(date)
  const da = new Date(date)
  return da.getFullYear() + '年' + (da.getMonth() + 1) + '月' + da.getDate() + '日 ' + da.getHours() + '时' + da.getMinutes() + '分'
}

function formateNoHours(date) {
  if (date === '' || date == null) {
    return ''
  }
  date = parseInt(date)
  const da = new Date(date)
  return da.getFullYear() + '-' + (da.getMonth() + 1) + '-' + da.getDate()
}

function formateHours(date) {
  if (date === '' || date == null) {
    return ''
  }
  date = parseInt(date)
  const da = new Date(date)
  return da.getHours() + ':' + da.getMinutes()
}

function formateNoHoursTime(strat, end) {
  return formateNoHours(strat) + ' ~ ' + formateNoHours(end)
}

function formateTime(strat, end) {
  return formateTimeToChinese(strat) + ' ~ ' + formateTimeToChinese(end)
}

function timeToNowStrning(date) {
  date = parseInt(date)
  const now = new Date().getTime()
  let t = now - date
  t = Math.trunc(t / 1000)
  if (t < 60) {
    return t + '秒前'
  }
  t = Math.trunc(t / 60)
  if (t < 60) {
    return t + '分钟前'
  }
  t = Math.trunc(t / 60)
  if (t < 24) {
    return t + '小时前'
  }
  t = Math.trunc(t / 24)
  if (t < 7) {
    return t + '天前'
  }
  return renderTime(date)
}

export default {
  renderTime,
  formateTimeToChinese,
  formateTime,
  formateNoHours,
  formateNoHoursTime,
  formateHours,
  timeToNowStrning
}
</script>
