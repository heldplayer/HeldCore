package net.specialattack.forge.core.client.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.*;

public final class GLUtil {

    public static boolean SHADERS_SUPPORTED = false;
    public static boolean ARB_SHADING = false;
    public static int GL_LINK_STATUS, GL_COMPILE_STATUS, GL_VERTEX_SHADER, GL_FRAGMENT_SHADER;

    private GLUtil() {
    }

    public static void initialize() {
        ContextCapabilities capabilities = GLContext.getCapabilities();
        GLUtil.SHADERS_SUPPORTED = capabilities.OpenGL21 || capabilities.GL_ARB_vertex_shader && capabilities.GL_ARB_fragment_shader && capabilities.GL_ARB_shader_objects;

        if (GLUtil.SHADERS_SUPPORTED) {
            if (capabilities.OpenGL21) {
                GLUtil.ARB_SHADING = false;
                GLUtil.GL_LINK_STATUS = GL20.GL_LINK_STATUS;
                GLUtil.GL_COMPILE_STATUS = GL20.GL_COMPILE_STATUS;
                GLUtil.GL_VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
                GLUtil.GL_FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;
            } else {
                GLUtil.ARB_SHADING = true;
                GLUtil.GL_LINK_STATUS = ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB;
                GLUtil.GL_COMPILE_STATUS = ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB;
                GLUtil.GL_VERTEX_SHADER = ARBVertexShader.GL_VERTEX_SHADER_ARB;
                GLUtil.GL_FRAGMENT_SHADER = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
            }
        }
    }

    public static int createShaderProgram() {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glCreateProgramObjectARB();
            } else {
                return GL20.glCreateProgram();
            }
        }
        return -1;
    }

    public static int createVertexShader() {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glCreateShaderObjectARB(GLUtil.GL_VERTEX_SHADER);
            } else {
                return GL20.glCreateShader(GLUtil.GL_VERTEX_SHADER);
            }
        }
        return -1;
    }

    public static int createFragmentShader() {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glCreateShaderObjectARB(GLUtil.GL_FRAGMENT_SHADER);
            } else {
                return GL20.glCreateShader(GLUtil.GL_FRAGMENT_SHADER);
            }
        }
        return -1;
    }

    public static int getProgramParameter(int id, int parameter) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glGetObjectParameteriARB(id, parameter);
            } else {
                return GL20.glGetProgrami(id, parameter);
            }
        }
        return -1;
    }

    public static int getShaderParameter(int id, int parameter) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glGetObjectParameteriARB(id, parameter);
            } else {
                return GL20.glGetShaderi(id, parameter);
            }
        }
        return -1;
    }

    public static String getShaderLog(int id, int maxLength) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glGetInfoLogARB(id, maxLength);
            } else {
                return GL20.glGetProgramInfoLog(id, maxLength);
            }
        }
        return "";
    }

    public static void setShaderSource(int id, String source) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glShaderSourceARB(id, source);
            } else {
                GL20.glShaderSource(id, source);
            }
        }
    }

    public static void compileShader(int id) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glCompileShaderARB(id);
            } else {
                GL20.glCompileShader(id);
            }
        }
    }

    public static void attachShader(int program, int shader) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glAttachObjectARB(program, shader);
            } else {
                GL20.glAttachShader(program, shader);
            }
        }
    }

    public static void linkProgram(int program) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glLinkProgramARB(program);
            } else {
                GL20.glLinkProgram(program);
            }
        }
    }

    public static void detachShader(int program, int shader) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glDetachObjectARB(program, shader);
            } else {
                GL20.glDetachShader(program, shader);
            }
        }
    }

    public static void deleteShader(int id) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glDeleteObjectARB(id);
            } else {
                GL20.glDeleteShader(id);
            }
        }
    }

    public static void deleteProgram(int id) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glDeleteObjectARB(id);
            } else {
                GL20.glDeleteProgram(id);
            }
        }
    }

    public static void useProgram(int id) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                ARBShaderObjects.glUseProgramObjectARB(id);
            } else {
                GL20.glUseProgram(id);
            }
        }
    }

    public static int getUniformLocation(int program, String name) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBShaderObjects.glGetUniformLocationARB(program, name);
            } else {
                return GL20.glGetUniformLocation(program, name);
            }
        }
        return 0;
    }

    public static int getAttributeLocation(int program, String name) {
        if (GLUtil.SHADERS_SUPPORTED) {
            if (GLUtil.ARB_SHADING) {
                return ARBVertexShader.glGetAttribLocationARB(program, name);
            } else {
                return GL20.glGetAttribLocation(program, name);
            }
        }
        return 0;
    }

    public static final class Uniform {

        private Uniform() {
        }

        public static void set1(int location, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform1ARB(location, buffer);
                } else {
                    GL20.glUniform1(location, buffer);
                }
            }
        }

        public static void set1(int location, float a) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform1fARB(location, a);
                } else {
                    GL20.glUniform1f(location, a);
                }
            }
        }

        public static void set1(int location, IntBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform1ARB(location, buffer);
                } else {
                    GL20.glUniform1(location, buffer);
                }
            }
        }

        public static void set1(int location, int a) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform1iARB(location, a);
                } else {
                    GL20.glUniform1i(location, a);
                }
            }
        }

        public static void set2(int location, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform2ARB(location, buffer);
                } else {
                    GL20.glUniform2(location, buffer);
                }
            }
        }

        public static void set2(int location, float a, float b) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform2fARB(location, a, b);
                } else {
                    GL20.glUniform2f(location, a, b);
                }
            }
        }

        public static void set2(int location, IntBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform2ARB(location, buffer);
                } else {
                    GL20.glUniform2(location, buffer);
                }
            }
        }

        public static void set2(int location, int a, int b) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform2iARB(location, a, b);
                } else {
                    GL20.glUniform2i(location, a, b);
                }
            }
        }

        public static void set3(int location, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform3ARB(location, buffer);
                } else {
                    GL20.glUniform3(location, buffer);
                }
            }
        }

        public static void set3(int location, float a, float b, float c) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform3fARB(location, a, b, c);
                } else {
                    GL20.glUniform3f(location, a, b, c);
                }
            }
        }

        public static void set3(int location, IntBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform3ARB(location, buffer);
                } else {
                    GL20.glUniform3(location, buffer);
                }
            }
        }

        public static void set3(int location, int a, int b, int c) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform3iARB(location, a, b, c);
                } else {
                    GL20.glUniform3i(location, a, b, c);
                }
            }
        }

        public static void set4(int location, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform4ARB(location, buffer);
                } else {
                    GL20.glUniform4(location, buffer);
                }
            }
        }

        public static void set4(int location, float a, float b, float c, float d) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform4fARB(location, a, b, c, d);
                } else {
                    GL20.glUniform4f(location, a, b, c, d);
                }
            }
        }

        public static void set4(int location, IntBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform4ARB(location, buffer);
                } else {
                    GL20.glUniform4(location, buffer);
                }
            }
        }

        public static void set4(int location, int a, int b, int c, int d) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniform4iARB(location, a, b, c, d);
                } else {
                    GL20.glUniform4i(location, a, b, c, d);
                }
            }
        }

        public static void setMatrix2(int location, boolean transpose, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniformMatrix2ARB(location, transpose, buffer);
                } else {
                    GL20.glUniformMatrix2(location, transpose, buffer);
                }
            }
        }

        public static void setMatrix3(int location, boolean transpose, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniformMatrix3ARB(location, transpose, buffer);
                } else {
                    GL20.glUniformMatrix3(location, transpose, buffer);
                }
            }
        }

        public static void setMatrix4(int location, boolean transpose, FloatBuffer buffer) {
            if (GLUtil.SHADERS_SUPPORTED) {
                if (GLUtil.ARB_SHADING) {
                    ARBShaderObjects.glUniformMatrix4ARB(location, transpose, buffer);
                } else {
                    GL20.glUniformMatrix4(location, transpose, buffer);
                }
            }
        }
    }

}
