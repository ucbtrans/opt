/**
 * Copyright (c) 2020, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **/
package opt.utils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;
import opt.data.AbstractLink.Type;
import opt.data.Commodity;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.control.AbstractController;

/**
 * Routines for manipulating OPT controllers.
 * 
 * @author Alex Kurzhanskiy
 */
public class ControlUtils {
    
//    public static boolean controllerOverlap(AbstractController a, AbstractController b) {
//        boolean res = false;
//
//        int a_start = Math.round(a.getStartTime());
//        int a_end = Math.round(a.getEndTime());
//        int b_start = Math.round(b.getStartTime());
//        int b_end = Math.round(b.getEndTime());
//
//        if (a.getId() == b.getId()) // skip self
//            return res;
//
//        if ((a_end <= b_start) || (a_start >= b_end)) // no time overlap
//            return res;
//
//        Set<LaneGroupType> a_lg_set = a.get_lanegroup_types();
//        Set<LaneGroupType> b_lg_set = b.get_lanegroup_types();
//        for (LaneGroupType lg : b_lg_set) {
//            if (a_lg_set.contains(lg)) {
//                res = true;
//                break;
//            }
//        }
//
//        return res;
//    }
    
}
