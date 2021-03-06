/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haoji.haoji.video.record.camera.filter.base.gpuimage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.haoji.haoji.video.record.camera.filter.prop.PropDynamicFilter;
import com.haoji.haoji.video.record.camera.utils.Rotation;
import com.haoji.haoji.video.record.camera.utils.TextureRotationUtil;


/**
 * Resembles a filter that consists of multiple filters applied after each
 * other.
 */
public class GPUImageFilterGroup extends GPUImageFilter {

    protected List<GPUImageFilter> mFilters;
    protected List<GPUImageFilter> mMergedFilters;
    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLTextureFlipBuffer;

    /**
     * Instantiates a new GPUImageFilterGroup with no filters.
     */
    public GPUImageFilterGroup() {
        this(null);
    }

    /**
     * Instantiates a new GPUImageFilterGroup with the given filters.
     *
     * @param filters the filters which represent this filter
     */
    public GPUImageFilterGroup(List<GPUImageFilter> filters) {
        mFilters = filters;
        if (mFilters == null) {
            mFilters = new ArrayList<GPUImageFilter>();
        } else {
            updateMergedFilters();
        }

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE_BAAB.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE_BAAB).position(0);

        mGLTextureBuffer = ByteBuffer
                .allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        mGLTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureFlipBuffer.put(flipTexture).position(0);
    }

    public void addFilter(GPUImageFilter aFilter) {
        if (aFilter == null) {
            return;
        }
        mFilters.add(aFilter);
        updateMergedFilters();
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        for (GPUImageFilter filter : mFilters) {
            filter.init();
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onDestroy()
     */
    @Override
    public void onDestroy() {
        destroyFramebuffers();
        for (GPUImageFilter filter : mFilters) {
            filter.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.cyberagent.android.gpuimage.GPUImageFilter#onOutputSizeChanged(int,
     * int)
     */
    @Override
    public void onDisplaySizeChanged(final int width, final int height) {
        super.onDisplaySizeChanged(width, height);
        if (mFrameBuffers != null) {
            destroyFramebuffers();
        }

        int size = mFilters.size();
        for (int i = 0; i < size; i++) {
            mFilters.get(i).onDisplaySizeChanged(width, height);
        }

        if (mMergedFilters != null && mMergedFilters.size() > 0) {
            size = mMergedFilters.size();
            mFrameBuffers = new int[size - 1];
            mFrameBufferTextures = new int[size - 1];

            for (int i = 0; i < size - 1; i++) {
                GLES20.glGenFramebuffers(1, mFrameBuffers, i);
                GLES20.glGenTextures(1, mFrameBufferTextures, i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

//    /*
//     * (non-Javadoc)
//     * @see
//     * jp.co.cyberagent.android.gpuimage.GPUImageFilter#onOutputSizeChanged(int,
//     * int)
//     */
//    @Override
//    public void onInputSizeChanged(final int width, final int height) {
//        super.onInputSizeChanged(width, height);
//        if (mFrameBuffers != null) {
//            destroyFramebuffers();
//        }
//
//        int size = mFilters.size();
//        for (int i = 0; i < size; i++) {
//            mFilters.get(i).onInputSizeChanged(width, height);
//        }
//
//        if (mMergedFilters != null && mMergedFilters.size() > 0) {
//            size = mMergedFilters.size();
//            mFrameBuffers = new int[size - 1];
//            mFrameBufferTextures = new int[size - 1];
//
//            for (int i = 0; i < size - 1; i++) {
//                GLES20.glGenFramebuffers(1, mFrameBuffers, i);
//                GLES20.glGenTextures(1, mFrameBufferTextures, i);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i]);
//                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
//                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//
//                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
//                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i], 0);
//
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//            }
//        }
//    }


    public void setFacePosition(int width,int height,PointF[] pointFs){
        if(mMergedFilters == null || mMergedFilters.isEmpty()){
            return ;
        }
        synchronized (mMergedFilters){
            for(GPUImageFilter gpuImageFilter:mMergedFilters){
                if(gpuImageFilter instanceof GPUImageFilterGroup){
                    ((GPUImageFilterGroup) gpuImageFilter).setFacePosition(width,height,pointFs);
                }
                if(gpuImageFilter instanceof PropDynamicFilter){
                    ((PropDynamicFilter)gpuImageFilter).setFacePosition(width,height,pointFs);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onDraw(int,
     * java.nio.FloatBuffer, java.nio.FloatBuffer)
     */
    @SuppressLint("WrongCall")
    @Override
    public int onDrawFrame(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        runPendingOnDrawTasks();
        if (!isInitialized() || mFrameBuffers == null || mFrameBufferTextures == null) {
            return textureId;
        }
        if (mMergedFilters != null) {
            int size = mMergedFilters.size();
            int previousTexture = textureId;
            for (int i = 0; i < size; i++) {
                GPUImageFilter filter = mMergedFilters.get(i);
                boolean isNotLast = i < size - 1;
                if (isNotLast && i < mFrameBuffers.length) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                    GLES20.glClearColor(0, 0, 0, 0);
                }

                if (i == 0) {
                    filter.onDrawFrame(previousTexture, cubeBuffer, textureBuffer);
                } else if (i == size - 1) {
//                    filter.onDrawFrame(previousTexture, mGLCubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer);
                    filter.onDrawFrame(previousTexture, mGLCubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer);
                } else {
                    filter.onDrawFrame(previousTexture, mGLCubeBuffer, mGLTextureBuffer);
                }

                if (isNotLast && i < mFrameBufferTextures.length) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    previousTexture = mFrameBufferTextures[i];
                }
            }
        }
        return textureId;
     }

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public List<GPUImageFilter> getFilters() {
        return mFilters;
    }

    public List<GPUImageFilter> getMergedFilters() {
        return mMergedFilters;
    }

//    @Override
//    public void onInputSizeChanged(int width, int height) {
//        for (GPUImageFilter filter : mMergedFilters) {
//            filter.onInputSizeChanged(width,height);
//        }
//        super.onInputSizeChanged(width, height);
//    }


    public void updateMergedFilters() {
        if (mFilters == null) {
            return;
        }
        if (mMergedFilters == null) {
            mMergedFilters = new ArrayList<GPUImageFilter>();
        }

        synchronized (mMergedFilters){
            if(mMergedFilters != null){
                mMergedFilters.clear();
            }
            List<GPUImageFilter> filters;
            for (GPUImageFilter filter : mFilters) {
                if (filter instanceof GPUImageFilterGroup) {
                    ((GPUImageFilterGroup) filter).updateMergedFilters();
                    filters = ((GPUImageFilterGroup) filter).getMergedFilters();
                    if (filters == null || filters.isEmpty())
                        continue;
                    mMergedFilters.addAll(filters);
                    continue;
                }
                mMergedFilters.add(filter);
            }
        }
    }
}
