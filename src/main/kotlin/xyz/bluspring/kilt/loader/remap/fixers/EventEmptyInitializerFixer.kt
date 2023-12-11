package xyz.bluspring.kilt.loader.remap.fixers

import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.lang.reflect.Modifier

// This used to be the CommonSuperFixer's job,
// but it has caused me so many genuine problems that I've just decided to rewrite it.
// Granted, it's probably the exact same, but running on every single class, because I can't be bothered dealing with its issues.
object EventEmptyInitializerFixer {
    fun fixClass(classNode: ClassNode) {
        // let's just run a fixer for everything. i can't be bothered at this point.
        if (Modifier.isInterface(classNode.access) || classNode.access and Opcodes.ACC_ENUM != 0)
            return

        // let's just kindly ignore these..
        if (classNode.name.replace(Regex("[0-9]"), "").endsWith("$") && classNode.outerClass != null)
            return

        // check if the class isn't static and is an inner class
        // also ensure it's not a record
        val isStatic = !(!Modifier.isStatic(classNode.access) && classNode.outerClass != null) || classNode.superName == "java/lang/Record"

        if (classNode.methods.any { m -> m.name == "<init>" && m.desc == "()V" })
            return

        if (classNode.methods.any { m -> m.name == "<init>" && m.desc == "(L${classNode.outerClass};)V" })
            return

        // Manually calculate the stack size, as otherwise the ClassWriter has a stroke.
        var stackSize = 1
        val initMethod = classNode.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_SYNTHETIC, "<init>", if (!isStatic) "(L${classNode.outerClass};)V" else "()V", null, null)
        val firstInitMethod = classNode.methods.firstOrNull { it.name == "<init>" }

        initMethod.visitCode()

        val label0 = Label()
        initMethod.visitLabel(label0)

        if (!isStatic) {
            // There exists an inner "this" that is invisible at compile-time, and is initialized
            // using the params provided by the initializer.
            initMethod.visitVarInsn(Opcodes.ALOAD, 0)
            initMethod.visitVarInsn(Opcodes.ALOAD, 1)
            initMethod.visitFieldInsn(Opcodes.PUTFIELD, classNode.name, "this$0", "L${classNode.outerClass};")
        }

        initMethod.visitVarInsn(Opcodes.ALOAD, 0)
        if (firstInitMethod != null) { // init this itself
            val methodType = Type.getMethodType(firstInitMethod.desc)
            methodType.argumentTypes.forEach { arg ->
                when (arg.descriptor) {
                    "I" -> {
                        initMethod.visitInsn(Opcodes.ICONST_0)
                    } // int
                    "F" -> initMethod.visitInsn(Opcodes.FCONST_0) // float
                    "D" -> initMethod.visitInsn(Opcodes.DCONST_0) // double
                    "J" -> initMethod.visitInsn(Opcodes.LCONST_0) // long
                    "Z" -> initMethod.visitInsn(Opcodes.ICONST_0) // boolean
                    "S" -> initMethod.visitInsn(Opcodes.ICONST_0) // short
                    "B" -> initMethod.visitInsn(Opcodes.ICONST_0) // byte

                    else -> initMethod.visitInsn(Opcodes.ACONST_NULL)
                }

                stackSize += when (arg.descriptor) {
                    "D", "J" -> 2 // doubles and longs are, of course, 64-bit.

                    else -> 1
                }
            }

            initMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.name, "<init>", firstInitMethod.desc, false)
        } else {
            initMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, classNode.superName, "<init>", "()V", false)
        }

        initMethod.visitInsn(Opcodes.RETURN)

        val label1 = Label()
        initMethod.visitLabel(label1)

        initMethod.visitLocalVariable("this", "L${classNode.name};", null, label0, label1, 0)
        if (!isStatic) {
            initMethod.visitLocalVariable("this$0", "L${classNode.outerClass};", null, label0, label1, 1)
            initMethod.visitMaxs(stackSize, 2)
        } else {
            initMethod.visitMaxs(stackSize, 1)
        }
        initMethod.visitEnd()
    }
}