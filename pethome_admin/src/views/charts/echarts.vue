<template>
    <section class="chart-container">
        <el-row>
            <el-col :span="12">
                <div id="chartColumn" style="width:100%; height:400px;"></div>
            </el-col>
            <el-col :span="12">
                <div id="chartBar" style="width:100%; height:400px;"></div>
            </el-col>
            <el-col :span="12">
                <div id="chartLine" style="width:100%; height:400px;"></div>
            </el-col>
            <el-col :span="12">
                <div id="chartPie" style="width:100%; height:400px;"></div>
            </el-col>
            <el-col :span="24">
                <a href="http://echarts.baidu.com/examples.html" target="_blank" style="float: right;">more>></a>
            </el-col>
        </el-row>
    </section>
</template>

<script>

import echarts from 'echarts'

export default {
  data() {
    return {
      chartColumn: null,
      chartBar: null,
      chartLine: null,
      chartPie: null
    }
  },

  methods: {
    // 1. 柱状图 (Column Chart)
    drawColumnChart() {
      this.$http.get("/org/shop/chartColumn")
          .then(resp => {
            if (resp.data.success) {
              this.chartColumn = echarts.init(document.getElementById('chartColumn'));
              this.chartColumn.setOption({
                title: { text: '店铺状态统计 (柱状图)' },
                tooltip: {},
                xAxis: {
                  data: resp.data.data.states_x // 后端返回的中文状态名
                },
                yAxis: {},
                series: [{
                  name: '数量',
                  type: 'bar',
                  data: resp.data.data.num_y // 后端返回的数字
                }]
              });
            } else {
              this.$message.error(resp.data.message);
            }
          })
          .catch(error => console.error(error));
    },

    // 2. 条形图 (Bar Chart) - X轴Y轴互换
    drawBarChart() {
      this.$http.get("/org/shop/chartBar")
          .then(resp => {
            if (resp.data.success) {
              this.chartBar = echarts.init(document.getElementById('chartBar'));
              this.chartBar.setOption({
                title: { text: '店铺状态统计 (条形图)' },
                tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
                grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
                // 注意：条形图把 category 放在 Y 轴
                xAxis: { type: 'value', boundaryGap: [0, 0.01] },
                yAxis: {
                  type: 'category',
                  data: resp.data.data.states_x // 数据放 Y 轴
                },
                series: [{
                  name: '数量',
                  type: 'bar',
                  data: resp.data.data.num_y // 数据对应过来
                }]
              });
            }
          });
    },

    // 3. 折线图 (Line Chart)
    drawLineChart() {
      this.$http.get("/org/shop/chartLine")
          .then(resp => {
            if (resp.data.success) {
              this.chartLine = echarts.init(document.getElementById('chartLine'));
              this.chartLine.setOption({
                title: { text: '店铺状态趋势 (折线图示例)' },
                tooltip: { trigger: 'axis' },
                xAxis: {
                  type: 'category',
                  boundaryGap: false,
                  data: resp.data.data.states_x
                },
                yAxis: { type: 'value' },
                series: [{
                  name: '数量',
                  type: 'line',
                  smooth: true, // 让线条圆滑一点
                  areaStyle: {}, // 填充颜色
                  data: resp.data.data.num_y
                }]
              });
            }
          });
    },

    // 4. 饼图 (Pie Chart) - 数据结构不同
    drawPieChart() {
      this.$http.get("/org/shop/chartPie")
          .then(resp => {
            if (resp.data.success) {
              this.chartPie = echarts.init(document.getElementById('chartPie'));
              this.chartPie.setOption({
                title: {
                  text: '店铺状态占比',
                  subtext: '实时数据',
                  x: 'center'
                },
                tooltip: {
                  trigger: 'item',
                  formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                  orient: 'vertical',
                  left: 'left',
                  // 自动从 series 数据中获取图例，不需要手动写 data
                },
                series: [{
                  name: '访问来源',
                  type: 'pie',
                  radius: '55%',
                  center: ['50%', '60%'],
                  // 重点：这里直接把后端返回的 List<Map> 塞进去
                  // 格式必须是: [{name: '待审核', value: 10}, ...]
                  data: resp.data.data,
                  itemStyle: {
                    emphasis: {
                      shadowBlur: 10,
                      shadowOffsetX: 0,
                      shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                  }
                }]
              });
            }
          });
    },

    drawCharts() {
      this.drawColumnChart();
      this.drawBarChart();
      this.drawLineChart();
      this.drawPieChart();
    },
  },

  mounted: function () {
    this.drawCharts();
  },

  // 注意：updated 钩子可能会导致频繁重绘，建议先注释掉，除非你有特殊需求
  // updated: function () {
  //     this.drawCharts()
  // }
}
</script>

<style scoped>
    .chart-container {
        width: 100%;
        float: left;
    }
    /*.chart div {
        height: 400px;
        float: left;
    }*/

    .el-col {
        padding: 30px 20px;
    }
</style>
