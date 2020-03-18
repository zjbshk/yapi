import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

def dealHTML(exportStr, Document doc) {


    // 导入脚本文件
    String scriptHtml = '''
            <link rel="stylesheet" type="text/css" href="js/layui/dist/css/layui.css"/>
            <script src="js/ScrollMagic.min.js" type="text/javascript"></script>
            <script src="js/clipboard.min.js" type="text/javascript"></script>
            <script src="js/layui/dist/layui.js" type="text/javascript" charset="utf-8"></script>
            <script src="js/index.js" type="text/javascript" charset="utf-8"></script>
            <link rel="stylesheet" href="css/default.css"/>
            <script src="js/highlight.min.js"></script>
            <script>hljs.initHighlightingOnLoad();</script>
            <link rel="stylesheet" href="css/index.css">'''
    def titleEle = doc.getElementsByTag("title").first()
    titleEle.after(scriptHtml)

    // 自定义浏览器的icon
    doc.head().prepend('<link rel="shortcut icon" href="img/favicon.png" type="image/x-icon">')

    // 设置跳转到搭建的yapi
    doc.select(".nav > a").first()
            .attr("href", "http://yapi.infomany.cn")
            .attr("target", "_blank")

    // 设置公司的icon
    doc.select("svg.svg").first().parent().remove()
    doc.select(".m-header").first().prepend("<img class='icon' src='img/favicon.png'/>")


    // 将title改为当前项目名称
    doc.select("h1.title").first().text(doc.select("h1.curproject-name").first().text())

    // 添加小火箭
    doc.body().append('<img id="go_top" class="go_top" src="img/go_top.png"/>')

    // 添加备案号
    doc.getElementById("right")
            .append('<a href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=20000996" target="_blank">赣ICP备20000996</a>')

    return doc.toString()
}