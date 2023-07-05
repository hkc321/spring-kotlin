package config

import com.epages.restdocs.apispec.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl

interface RestdocsTestDsl {
    fun ResultActions.andDocument(
        identifier: String,
        vararg snippets: Snippet
    ): ResultActions =
        andDo(
            MockMvcRestDocumentationWrapper.document(
                identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                *snippets
            )
        )

    fun ResultActionsDsl.andDocument(
        identifier: String,
        vararg snippets: Snippet
    ): ResultActionsDsl =
        andDo {
            MockMvcRestDocumentationWrapper.document(
                identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                *snippets
            )
        }

    fun makeSnippets(resourceSnippetParameters: ResourceSnippetParameters): Array<ResourceSnippet> =
        arrayOf(ResourceDocumentation.resource(resourceSnippetParameters))

    fun field(path: String, type: JsonFieldType, description: String, optional: Boolean = false): FieldDescriptor {
        val field = PayloadDocumentation.fieldWithPath(path)
            .type(type)
            .description(description)
        if (optional) {
            field.optional()
        }
        return field
    }

    fun parameter(
        path: String,
        type: SimpleType,
        description: String,
        optional: Boolean = false
    ): ParameterDescriptorWithType {
        val parameter = ResourceDocumentation.parameterWithName(path)
            .type(type)
            .description(description)
        if (optional) {
            parameter.optional()
        }
        return parameter
    }


    fun header(name: String, description: String): HeaderDescriptor =
        HeaderDocumentation.headerWithName(name).description(description)

    fun exist(): Matcher<Any> =
        Matchers.notNullValue()
}