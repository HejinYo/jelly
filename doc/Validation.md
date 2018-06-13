hibernate validator constraint 注解

注解详细 验证规则 说明：
    http://jinnianshilongnian.iteye.com/blog/1733708

hibernate validator详细说明：
    http://jinnianshilongnian.iteye.com/blog/1990081

Bean Validation 中内置的 constraint
@Null   被注释的元素必须为 null
@NotNull    被注释的元素必须不为 null
@AssertTrue     被注释的元素必须为 true
@AssertFalse    被注释的元素必须为 false
@Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Size(max=, min=)   被注释的元素的大小必须在指定的范围内
@Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内
@Past   被注释的元素必须是一个过去的日期
@Future     被注释的元素必须是一个将来的日期
@Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式  如：@Pattern(regexp = "^[a-zA-Z_][\\w]{4,19}$", message="{user.name.error}")

Hibernate Validator 附加的 constraint
@NotBlank(message =)   验证字符串非null，且长度必须大于0
@Email  被注释的元素必须是电子邮箱地址
@Length(min=,max=)  被注释的字符串的大小必须在指定的范围内
@NotEmpty   被注释的字符串的必须非空
@Range(min=,max=,message=)  被注释的元素必须在合适的范围内


1.6、错误页面：
Java代码  收藏代码
<spring:hasBindErrors name="user">
    <c:if test="${errors.fieldErrorCount > 0}">
        字段错误：<br/>
        <c:forEach items="${errors.fieldErrors}" var="error">
            <spring:message var="message" code="${error.code}" arguments="${error.arguments}" text="${error.defaultMessage}"/>
            ${error.field}------${message}<br/>
        </c:forEach>
    </c:if>

    <c:if test="${errors.globalErrorCount > 0}">
        全局错误：<br/>
        <c:forEach items="${errors.globalErrors}" var="error">
            <spring:message var="message" code="${error.code}" arguments="${error.arguments}" text="${error.defaultMessage}"/>
            <c:if test="${not empty message}">
                ${message}<br/>
            </c:if>
        </c:forEach>
    </c:if>
</spring:hasBindErrors>

大家以后可以根据这个做通用的错误消息显示规则。比如我前端页面使用validationEngine显示错误消息，
那么我可以定义一个tag来通用化错误消息的显示：showFieldError.tag。
---------------Statr showFieldError.tag----------------------
<%@ tag pageEncoding="UTF-8" description="显示字段错误消息" %>
<%@ attribute name="commandName" type="java.lang.String" required="true" description="命令对象名称" %>
<%@ attribute name="errorPosition" type="java.lang.String" required="false" description="错误消息位置，可以是 topLeft, topRight, bottomLeft, centerRight, bottomRight" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:if test="${empty errorPosition}">
    <c:set var="errorPosition" value="topRight"/>
</c:if>
<spring:hasBindErrors name="${commandName}">
    <c:if test="${errors.fieldErrorCount > 0}">
    <c:forEach items="${errors.fieldErrors}" var="error">
        <spring:message var="message" code="${error.code}" arguments="${error.arguments}" text="${error.defaultMessage}"/>
        <c:if test="${not empty message}">
            $("[name='${error.field}']")
                    .validationEngine("showPrompt", "${message}", "error", "${errorPosition}", true)
                    .validationEngine("updatePromptsPosition");
        </c:if>
    </c:forEach>
    </c:if>
</spring:hasBindErrors>
---------------End showFieldError.tag----------------------


2、分组验证及分组顺序
如果我们想在新增的情况验证id和name，而修改的情况验证name和password，怎么办？ 那么就需要分组了。
首先定义分组接口：
Java代码
    public interface First {
    }

    public interface Second {
    }
分组接口就是两个普通的接口，用于标识，类似于java.io.Serializable。

接着我们使用分组接口标识实体：
Java代码
public class User implements Serializable {

    @NotNull(message = "{user.id.null}", groups = {First.class})
    private Long id;

    @Length(min = 5, max = 20, message = "{user.name.length.illegal}", groups = {Second.class})
    @Pattern(regexp = "[a-zA-Z]{5,20}", message = "{user.name.illegal}", groups = {Second.class})
    private String name;

    @NotNull(message = "{user.password.null}", groups = {First.class, Second.class})
    private String password;
}

验证时使用如：
Java代码
@RequestMapping("/save")
public String save(@Validated({Second.class}) User user, BindingResult result) {
    if(result.hasErrors()) {
        return "error";
    }
    return "success";
}
即通过@Validate注解标识要验证的分组；如果要验证两个的话，可以这样@Validated({First.class, Second.class})。

接下来我们来看看通过分组来指定顺序；还记得之前的错误消息吗？ user.name会显示两个错误消息，而且顺序不确定；如果我们先验证一个消息；如果不通过再验证另一个怎么办？可以通过@GroupSequence指定分组验证顺序：

Java代码
@GroupSequence({First.class, Second.class, User.class})
public class User implements Serializable {
    private Long id;

    @Length(min = 5, max = 20, message = "{user.name.length.illegal}", groups = {First.class})
    @Pattern(regexp = "[a-zA-Z]{5,20}", message = "{user.name.illegal}", groups = {Second.class})
    private String name;

    private String password;
}
通过@GroupSequence指定验证顺序：先验证First分组，如果有错误立即返回而不会验证Second分组，接着如果First分组验证通过了，那么才去验证Second分组，最后指定User.class表示那些没有分组的在最后。这样我们就可以实现按顺序验证分组了。

另一个比较常见的就是级联验证：
如：
Java代码  收藏代码
public class User {

    @Valid
    @ConvertGroup(from=First.class, to=Second.class)
    private Organization o;

}
 1、级联验证只要在相应的字段上加@Valid即可，会进行级联验证；
 @ConvertGroup的作用是当验证o的分组是First时，那么验证o的分组是Second，即分组验证的转换。

3、消息中使用EL表达式
假设我们需要显示如：用户名[NAME]长度必须在[MIN]到[MAX]之间，此处大家可以看到，我们不想把一些数据写死，如NAME、MIN、MAX；此时我们可以使用EL表达式。

如：
Java代码
@Length(min = 5, max = 20, message = "{user.name.length.illegal}", groups = {First.class})
错误消息：
Java代码
user.name.length.illegal=用户名长度必须在{min}到{max}之间

其中我们可以使用{验证注解的属性}得到这些值；如{min}得到@Length中的min值；其他的也是类似的。

到此，我们还是无法得到出错的那个输入值，如name=zhangsan。此时就需要EL表达式的支持，首先确定引入EL jar包且版本正确。然后使用如：
Java代码
user.name.length.illegal=用户名[${validatedValue}]长度必须在5到20之间
使用如EL表达式：${validatedValue}得到输入的值，如zhangsan。
当然我们还可以使用如${min > 1 ? '大于1' : '小于等于1'}，及在EL表达式中也能拿到如@Length的min等数据。

另外我们还可以拿到一个java.util.Formatter类型的formatter变量进行格式化：
Java代码  收藏代码
${formatter.format("%04d", min)}