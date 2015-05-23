/*
 * PoreGradle
 * Copyright (c) 2015, Lapis <https://github.com/LapisBlue>
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
 */
package net.minecrell.gitpatcher.task

import static net.minecrell.gitpatcher.git.Patcher.openGit

import org.eclipse.jgit.submodule.SubmoduleStatus
import org.eclipse.jgit.submodule.SubmoduleStatusType
import org.gradle.api.tasks.TaskAction

class UpdateSubmodulesTask extends SubmoduleTask {

    @TaskAction
    void updateSubmodules() {
        openGit(repo) {
            SubmoduleStatus status = submoduleStatus().addPath(submodule).call().get(submodule);
            if (status.type == SubmoduleStatusType.INITIALIZED) {
                didWork = false
                return
            }

            if (status.type == SubmoduleStatusType.MISSING) {
                submoduleInit().addPath(submodule).call()
            } else if (status.type == SubmoduleStatusType.REV_CHECKED_OUT) {
                logger.warn "Resetting submodule \"{}\" from {} back to {}", submodule, status.headId, status.indexId
            }

            submoduleUpdate().addPath(submodule).call()
        }
    }

}