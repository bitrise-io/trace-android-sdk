package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.BaseExtension;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import okhttp3.OkHttpClient;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Helper class for doing the OkHttp related transforms in {@link TraceTransform}.
 */
public class OkHttpTransformHelper extends TransformHelper {

  OkHttpTransformHelper(@NonNull final Project project,
                        @NonNull final BaseExtension baseExtension,
                        @NonNull final Logger logger) {
    super(project, baseExtension, logger);
  }

  /**
   * Transforms {@link OkHttpClient#OkHttpClient()} to add our interceptor, the
   * TraceOkHttpInterceptor.
   *
   * @param transformInvocation the given {@link TransformInvocation}.
   * @param name                the unique name of the transform.
   * @param outputTypes         the type(s) of data that is generated by the Transform.
   * @param scopes              the scope(s) of the Transform. This indicates which scopes the
   *                            transform consumes.
   * @throws IOException            if any I/O exception occurs.
   * @throws NotFoundException      if the OkHttpClient class cannot be found.
   * @throws CannotCompileException if the new content for the constructor cannot be compiled.
   */
  void updateOkHttpConstructor(@NonNull final TransformInvocation transformInvocation,
                               @NonNull final String name,
                               @NonNull final Set<QualifiedContent.ContentType> outputTypes,
                               @NonNull final Set<? super QualifiedContent.Scope> scopes)
      throws IOException, NotFoundException, CannotCompileException {
    final File outputDirectory = transformInvocation.getOutputProvider()
                                                    .getContentLocation(name, outputTypes, scopes,
                                                        Format.DIRECTORY);
    final CtClass okHttpClientClass = findClass(transformInvocation, "okhttp3.OkHttpClient");
    final CtConstructor[] okHttpClientConstructors = okHttpClientClass.getConstructors();
    for (@NonNull final CtConstructor okHttpClientConstructor : okHttpClientConstructors) {
      if (okHttpClientConstructor.getModifiers() == AccessFlag.PUBLIC
          && okHttpClientConstructor.getMethodInfo().getDescriptor().equals("()V")) {
        okHttpClientConstructor.setBody(
            "{this(new okhttp3.OkHttpClient.Builder().addInterceptor(new io.bitrise.trace.data"
                + ".collector.network.okhttp.TraceOkHttpInterceptor()));}");
      }
    }
    okHttpClientClass.writeFile(outputDirectory.getCanonicalPath());
  }

  /**
   * Transforms {@link OkHttpClient.Builder#build()} method to have exactly one instance of our
   * interceptor, the TraceOkHttpInterceptor and it will must be the last Interceptor, so it will
   * contain the final network call.
   *
   * @param transformInvocation the given {@link TransformInvocation}.
   * @param name                the unique name of the transform.
   * @param outputTypes         the type(s) of data that is generated by the Transform.
   * @param scopes              the scope(s) of the Transform. This indicates which scopes the
   *                            transform consumes.
   * @throws IOException            if any I/O exception occurs.
   * @throws NotFoundException      if the OkHttpClient class cannot be found.
   * @throws CannotCompileException if the new content for the constructor cannot be compiled.
   */
  void updateOkHttpBuilder(@NonNull final TransformInvocation transformInvocation,
                           @NonNull final String name,
                           @NonNull final Set<QualifiedContent.ContentType> outputTypes,
                           @NonNull final Set<? super QualifiedContent.Scope> scopes)
      throws IOException, NotFoundException, CannotCompileException {
    final File outputDirectory = transformInvocation.getOutputProvider()
                                                    .getContentLocation(name, outputTypes, scopes,
                                                        Format.DIRECTORY);
    final CtClass okHttpClientBuilderClass =
        findClass(transformInvocation, "okhttp3.OkHttpClient$Builder");
    final CtMethod okHttpBuilderBuildMethod = okHttpClientBuilderClass.getDeclaredMethod("build");
    okHttpBuilderBuildMethod.setBody(getBuildMethodContent());
    okHttpClientBuilderClass.writeFile(outputDirectory.getCanonicalPath());
  }

  /**
   * Gets the content we should add for {@link OkHttpClient.Builder#build()}. This should remove
   * all the previously added TraceOkHttpInterceptors and add a new one.
   *
   * @return the content.
   */
  @NonNull
  private String getBuildMethodContent() {
    return "{java.util.Iterator interceptorIterator = interceptors.iterator();"
        + "while (interceptorIterator.hasNext()){"
        + "okhttp3.Interceptor interceptor = (okhttp3.Interceptor) interceptorIterator.next();"
        + "if (interceptor instanceof io.bitrise.trace.data.collector.network.okhttp"
        + ".TraceOkHttpInterceptor){"
        + "interceptorIterator.remove();"
        + "}"
        + "}"
        + "interceptors.add(new io.bitrise.trace.data.collector.network.okhttp"
        + ".TraceOkHttpInterceptor());"
        + "return new okhttp3.OkHttpClient(this);}";
  }
}
