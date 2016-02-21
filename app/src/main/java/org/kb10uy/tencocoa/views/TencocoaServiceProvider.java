package org.kb10uy.tencocoa.views;

import org.kb10uy.tencocoa.TencocoaReadPermissionService;
import org.kb10uy.tencocoa.TencocoaStreamingService;
import org.kb10uy.tencocoa.TencocoaWritePermissionService;

/**
 * Created by kb10uy on 2016/02/21.
 */
public interface TencocoaServiceProvider {
    TencocoaWritePermissionService getWritePermissionService();
    TencocoaReadPermissionService getReadPermissionService();
    TencocoaStreamingService getStreamingService();
    boolean isWriteBound();
    boolean isReadBound();
    boolean isStreamingBound();
}
