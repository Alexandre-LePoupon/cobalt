/**
 *
 * Cobalt.h
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

#import <Foundation/Foundation.h>

#import "CobaltViewController.h"

#define viewControllerDeallocatedNotification   @"viewControllerDeallocatedNotification"

#define haploidSpecialJSKey     @"cob@l7#k&y"

// CONFIGURATION FILE
#define confFileName            @"cobalt.conf"
#define kIos                    @"ios"
#define kControllers            @"controllers"
#define kPlugins                @"plugins"
#define kIosNibName             @"iosNibName"
#define kPullToRefreshEnabled   @"pullToRefresh"
#define kInfiniteScrollEnabled  @"infiniteScroll"

@interface Cobalt : NSObject

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark RESOURCE PATH
#pragma mark -
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*!
 @method		+ (void)setResourcePath:(NSString *)resourcePath;
 @param         resourcePath
 @abstract		this method sets the resource path for the whole application
 */
+ (void)setResourcePath:(NSString *)resourcePath;

/*!
 @method		+ (NSString *)resourcePath;
 @abstract		this method gets the resource path for the whole application
 */
+ (NSString *)resourcePath;


+ (NSDictionary *)getControllersConfiguration;
+ (NSDictionary *)getPluginsConfiguration;

+ (NSString *)stringWithContentsOfFile:(NSString *)path;

+ (id)JSONObjectWithString:(NSString *)string;

@end
