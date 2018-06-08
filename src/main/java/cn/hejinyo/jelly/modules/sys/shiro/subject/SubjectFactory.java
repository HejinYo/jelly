package cn.hejinyo.jelly.modules.sys.shiro.subject;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 禁用了session的SubjectFactory
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:04
 */
public class SubjectFactory extends DefaultWebSubjectFactory {
    @Override
    public Subject createSubject(SubjectContext context) {
        //不创建session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}
