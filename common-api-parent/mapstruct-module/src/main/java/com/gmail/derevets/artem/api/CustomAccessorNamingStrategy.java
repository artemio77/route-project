package com.gmail.derevets.artem.api;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

public class CustomAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    @Override
    public boolean isSetterMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        return methodName.startsWith("with") && methodName.length() > 4
                ||
                isBuilderSetter(method)
                ||
                super.isSetterMethod(method);
    }


    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        return isFluentGetter(method)
                || !methodName.startsWith("with") && method.getReturnType().getKind() != TypeKind.VOID && method
                .getParameters().isEmpty()
                || super
                .isGetterMethod(method);

    }

    protected boolean isFluentGetter(ExecutableElement executableElement) {
        return executableElement.getParameters().size() == 0 && executableElement.getReturnType().toString()
                .equals(executableElement.getEnclosingElement().asType().toString());
    }

    protected boolean isBuilderSetter(ExecutableElement getterOrSetterMethod) {
        return getterOrSetterMethod.getParameters().size() == 1 && getterOrSetterMethod.getReturnType().toString()
                .equals(getterOrSetterMethod.getEnclosingElement().asType().toString());
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
        String methodName = getterOrSetterMethod.getSimpleName().toString();

        if (methodName.startsWith("is") || methodName.startsWith("get") || methodName.startsWith("set")) {
            return super.getPropertyName(getterOrSetterMethod);
        } else if (methodName.length() > 4 && methodName.startsWith("with")) {
            return IntrospectorUtils.decapitalize(methodName.startsWith("with") ? methodName.substring(4) : methodName);
        } else if (isFluentGetter(getterOrSetterMethod) || this.isBuilderSetter(getterOrSetterMethod)) {
            return methodName;
        }
        return methodName;
    }

}