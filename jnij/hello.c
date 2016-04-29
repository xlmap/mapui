/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
#include <stdbool.h>
#include <android/log.h>
#define SIZE 1024
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

//定义函数宏动态获取数组长度，对于字符数组长度减去\0字符 len-1
#define GET_ARRAY_LEN(array,len) {\
	len = (sizeof(array) / sizeof(array[0]));\
}

/*
 *把二维数组保存成在MapData.java文件里
 */
 char *content[] = {
	 "package com.test.ui ;\n\n",
	 "import com.test.ui.* ;\n\n",
	 "public class MapData {\n\n\t",
	 "private int mapWidth = ",
	 "private int mapHeight = ",
	 "private int blockWidth = ",
	 "private int blockHeight = ",
	 "private long data[][] = {\n\n\t"
 };
	 
 //数组长度
 int len,count;
 const char *params[4];
 //二级指针用于获取content中的内容
 char **p = content;
 
/**
 * 参数jstring 返回值char*
 * convert方法 把java中的jstring的类型转化成一个c/c++中的char*字符串
 */
char* convert(JNIEnv* env,jstring jstr)
{
	 char* str = NULL;
	 jclass clsstring = (*env)->FindClass(env,"java/lang/String");
	 jstring strencode = (*env)->NewStringUTF(env,"UTF-8");
	 jmethodID mid = (*env)->GetMethodID(env,clsstring,"getBytes","(Ljava/lang/String;)[B");
	 jbyteArray arr = (jbyteArray)(*env)->CallObjectMethod(env,jstr,mid,strencode); 
	 jsize len = (*env)->GetArrayLength(env,arr);
	 jbyte* bt = (*env)->GetByteArrayElements(env,arr,JNI_FALSE);
	 if(len > 0) {
	  str = (char*)malloc(len+1);  
	  memcpy(str,bt,len);
	  str[len]=0;
	 }
	 (*env)->ReleaseByteArrayElements(env,arr,bt,0);  
	 return str;
}
 
void func(FILE* fp){
	//返回地图块宽度方法
	fprintf(fp,"\tpublic int getBlockWidth() {\n\t");
	fprintf(fp,"\treturn this.blockWidth;\n\t}\n");
	//返回地图块宽高度方法
	fprintf(fp,"\tpublic int getBlockHeight() {\n\t");
	fprintf(fp,"\treturn this.blockHeight;\n\t}\n");
	//返回地图宽度方法
	fprintf(fp,"\tpublic int getMapWidth() {\n\t");
	fprintf(fp,"\treturn this.mapWidth;\n\t}\n");
	//返回地图高度方法
	fprintf(fp,"\tpublic int getMapHeight() {\n\t");
	fprintf(fp,"\treturn this.blockHeight;\n\t}\n");
	//返回地图二维数组方法
	fprintf(fp,"\tpublic long[][] getData() {\n\t");
	fprintf(fp,"\treturn this.data;\n\t}\n");
	
}


 //保存地图的尺寸参数
 void 
 Java_com_map_ui_Native_passParams(JNIEnv* env,
 						jobject thiz,jstring mw,jstring mh,jstring bw,jstring bh){
	//params数组赋值
	params[0] = strcat(convert(env,mw)," ;\n\t");
	params[1] = strcat(convert(env,mh)," ;\n\t");
	params[2] = strcat(convert(env,bw)," ;\n\t");
	params[3] = strcat(convert(env,bh)," ;\n\t");
 }
 
//保存图片资源数组到ResId.java文件
 void 
 Java_com_map_ui_Native_imageRes(JNIEnv* env,
 						jobject thiz,jobjectArray imageRes){
 	int i ;
	int size = (*env)->GetArrayLength(env,imageRes);
	//保存资源图片id
	const char **resid[size];
	FILE *f = fopen("/sdcard/地图编辑器/Test/src/com/test/ui/ResId.java","w+");
	fprintf(f,"%s","package com.test.ui;\n\n");
	fprintf(f,"%s","import com.test.ui.*;\n\n");
	fprintf(f,"%s","public class ResId {\n\n\t");
	fprintf(f,"%s","private int id[] = {\n\t\t");
	for(i=0;i<size;i++){
		//从imageRes数组中得到一个jstring的数据
        jstring str = (*env)->GetObjectArrayElement(env,imageRes, i);
		//把jstring转换为char*类型
		resid[i] = (*env)->GetStringUTFChars(env,str,NULL);
		fprintf(f,"%s",resid[i]);
		if(i<size-1){
			fprintf(f,"%s",",");
		}
		if(i%3==0&&i>0){
			fprintf(f,"%s","\n");
		}
        //释放字符串指针
        (*env)->ReleaseStringUTFChars(env, str, resid[i]);
	}
	fprintf(f,"%s","\t};\n\n\t");
	fprintf(f,"%s","public int[] getResId() {\n\t\t");
	fprintf(f,"%s","return this.id ;\n\t}\n\n}");
	fclose(f);
 }
 
 
 
 
 jstring 
 Java_com_map_ui_Native_saveArray(JNIEnv* env,
 						jobject thiz,jobjectArray data){
	 int i,j,k;  
	 count = 0;
	 FILE *fp = fopen("/sdcard/地图编辑器/Test/src/com/test/ui/MapData.java","w+");
	 
     int row = (*env)->GetArrayLength(env, data)-1;//获得行数
	 //每一行都是一个数组
     jarray arr = ((*env)->GetObjectArrayElement(env, data, 0));  
  
     int col =(*env)->GetArrayLength(env, arr)-1; //获得列数  
	 //创建一个新的数组
	 long a[col][row];
     long b[row][col];
	 //获得content数组的长度
	 GET_ARRAY_LEN(content,len);
	 for(p=content;p<content+len;p++){
		 fprintf(fp,"%s",*p);
		 if(count>2&&count<len-1){
			 fprintf(fp,"%s",params[count-3]);
		 }
		 count++;
	 }
	  //进行数组的赋值
     for (i = 0; i < row; i++){ 
	 	 fprintf(fp,"%s","\n\t\t{");
	     //获得数组当前行数
         arr = ((*env)->GetObjectArrayElement(env, data, i));  
	     //获得当前这一行所对应的列
         jint *coldata = (*env)->GetLongArrayElements(env, (jlongArray)arr, 0 );  
         for (j=0; j<col*2; j++) {  
		 	//此处获取不到数组全部偶数列的数据,所以将数组的列扩大一倍col*2，获取全部列数据
		 	if(j%2==0){
               b [i][j/2] = coldata[j]; //取出JAVA类中data的数据,并赋值给JNI中的数组
			    fprintf(fp,"%d",b[i][j/2]);
			   //输出分号
				if(j/2<col-1){
					fprintf(fp,"%s",",");
				}
			}
         }  
		 fprintf(fp,"%s","}");
		 //输出分号
		 if(i<col-1){
			 fprintf(fp,"%s",",");
		 }
        (*env)->ReleaseLongArrayElements(env, (jlongArray)arr, coldata,0 );
     }
	 //输出"}"扩号
	 for(k=0;k<2;k++){
		 if(k==0){
			 fprintf(fp,"%s","\n\t};\n\n");
			 func(fp);
		 }else{
			 fprintf(fp,"%s","\n}");
		 }
	 }
	 fclose(fp);
	 return (*env)->NewStringUTF(env, "success");
 }
 
 
 
 //执行shell命令，c调用java中的方法，返回执行结果(此方法暂时还没有用到)
jstring
Java_com_map_ui_Native_exec( JNIEnv* env,
                                         jobject obj,jstring cmd){
	//找到MapUtils类
	//jclass cls = (*env)->FindClass(env,"com/map/utils/MapUtils");
	//找到MapUtils类中的fun方法id
	//jmethodID mid = (*env)->GetStaticMethodID(env,cls,"fun","(Ljava/lang/String;)V");
	const jbyte* bt = (*env)->GetStringUTFChars(env,cmd,NULL);
	char* str=(char *)bt;
	char buf[SIZE] = {0};
	memset(buf,0,SIZE);
	FILE *fp = popen(str,"r");
	while(fread(buf, SIZE-1, 1, fp)!=NULL){
		 //(*env)->CallStaticVoidMethod(env,cls,mid,(*env)->NewStringUTF(env,buf));
		 return (*env)->NewStringUTF(env,buf);
		 buf[SIZE-1] = 0;
	}
	pclose(fp);
}
 
 
jstring
Java_com_map_ui_Native_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}


jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	//JNIEnv* env = NULL;
	jint result = -1;

//	gs_JavaVM = vm;
//  g_vm=vm;
	/*
	if ((*vm)->GetEnv(vm, (void**) &jniEnv, JNI_VERSION_1_2) != JNI_OK) 
	 {
		fprintf(stderr, "ERROR: GetEnv failed\n");
		goto bail;
	}
	assert(jniEnv != NULL);

	if (register_natives(jniEnv) < 0) 
	 {
		fprintf(stderr, "ERROR: Exif native registration failed\n");
		goto bail;
	}

	/* success -- return valid version number 
	result = JNI_VERSION_1_2;
*/
bail:
 return JNI_VERSION_1_2;
 }
