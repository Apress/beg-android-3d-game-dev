#include <string.h>
#include <jni.h>



float Gravity = 0.010f;



// package = com.robsexample.glhelloworld;
// class = MyGLRenderer
jstring
Java_com_robsexample_glhelloworld_MyGLRenderer_RobsstringFromJNI(JNIEnv* env,
                                                      	  	  	 jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello World from JNI and Native Code.");
}

jfloat
Java_com_robsexample_glhelloworld_Physics_ApplyGravityToObjectNative(JNIEnv* env,
                                                      	       	   	 jobject thiz,
                                                      	             jfloat YAccel)
{
	YAccel = YAccel - Gravity;

	return YAccel;
}

Java_com_robsexample_glhelloworld_Physics_AddRotationNative(JNIEnv* env,
                                                      	    jobject thiz,
                                                      	    jobject Orient,
                                                      	    jfloat RotationAngle)
{
	/*
	GetObjectClass
	jclass GetObjectClass(JNIEnv *env, jobject obj);
	*/
	jclass OrientationClass = (*env)->GetObjectClass(env, Orient);

	/*
	GetMethodID
	jmethodID GetMethodID(JNIEnv *env, jclass clazz, const char *name, const char *sig);
	*/
	jmethodID  MethodID = (*env)->GetMethodID(env,
											  OrientationClass,
											  "AddRotation",
			                                  "(F)V");

	/*
	NativeType Call<type>Method(JNIEnv *env, jobject obj, jmethodID methodID, ...);
	*/
	(*env)->CallVoidMethod(env, Orient, MethodID, RotationAngle);

}

// dot product
float DotProduct(float x1, float y1, float z1,
				 float x2, float y2, float z2)
{
	return ((x1 * x2) + (y1 * y2) + (z1 * z2));
}

jfloat
Java_com_robsexample_glhelloworld_Physics_CalculateCollisionImpulseNative(JNIEnv* env,
                                                      	    			  jobject thiz,

                                                      	    			  jfloat CoefficientOfRestitution,

                                                      	                  jfloat Mass1,
                                                      	                  jfloat Mass2,

                                                      	                  jfloat RelativeVelocityX,
                                                      	                  jfloat RelativeVelocityY,
                                                      	                  jfloat RelativeVelocityZ,

                                                      	                  jfloat CollisionNormalX,
                                                      	                  jfloat CollisionNormalY,
                                                      	                  jfloat CollisionNormalZ)
{
		// 1. Calculate the impulse along the line of action of the Collision Normal
		//float Impulse = (-(1+CoefficientOfRestitution) * (RelativeVelocity.DotProduct(CollisionNormal))) /
		//		         (1/Mass1 + 1/Mass2);

		float RelativeVelocityDotCollisionNormal = DotProduct(RelativeVelocityX, RelativeVelocityY, RelativeVelocityZ,
													   CollisionNormalX, CollisionNormalY, CollisionNormalZ);

		float Impulse = (-(1+CoefficientOfRestitution) * RelativeVelocityDotCollisionNormal)/(1/Mass1 + 1/Mass2);

		return Impulse;
}



