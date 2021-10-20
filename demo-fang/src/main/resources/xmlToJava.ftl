<#if package??>
package ${package}

</#if>
<#if importClasses??>
<#list importClasses as importClass>
import ${importClass}
</#list>
</#if>

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "${rootName!""}")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ${className} {
<#if fieldList??>
<#list fieldList as field>

    @XmlElement(name = "${field.nodeName!""}")
    private ${field.className!""} ${field.fieldName!""};
</#list>
</#if>

<#if childClassList??>
<#list childClassList as childClass>
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor
    @AllArgsConstructor
    <#if childClass.needBuild>
    @Builder
    </#if>
    public static class ${childClass.className} {
        <#if childClass.fieldList??>
        <#list childClass.fieldList as childField>

        @XmlElement(name = "${childField.nodeName!""}")
        private ${childField.className!""} ${childField.fieldName!""};
        </#list>
        </#if>
    }

</#list>
</#if>
}
