/**
 * 드롭영역
 * @author GOONO
 * @since  2021
 * @version 1.0
 * @see
 * Copyright (C)  All right reserved.
 */
var coreDropZone = function (pageObj, cbFilePath, uploadFileCallBack) {
    this.initialize(pageObj, cbFilePath, uploadFileCallBack);
};

coreDropZone.prototype = {
    guid: coreCommon.genUUID(),
    enableDrop: true,
    //유니크한 값 만들기
    genUUID: function () {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3) | 0x8).toString(16);
        });
        return uuid;
    },
    enable: function () {
        this.enableDrop = true;
    },
    disable: function () {
        this.enableDrop = false;
    },
    //private
    initialize: function (pageObj, cbFilePath, uploadFileCallBack) {
        var self = this;

        pageObj.on('dragenter', function (e) {
            if (self.enableDrop == false) return;
            e.stopPropagation();
            e.preventDefault();
            $(this).removeClass('dragover');
        });

        pageObj.on('dragleave', function (e) {
            if (self.enableDrop == false) return;
            e.stopPropagation();
            e.preventDefault();
            $(this).removeClass('dragover');
        });

        pageObj.on('dragover', function (e) {
            if (self.enableDrop == false) return;
            e.stopPropagation();
            e.preventDefault();
            $(this).addClass('dragover');
        });

        //드롭후 파일처리
        pageObj.on('drop', function (e) {
            if (self.enableDrop == false) return;
            e.preventDefault();
            $(this).removeClass('dragover');

            var uploadItems = {dir: [], files: [], file_entries: [], dropFiles: [], dropFileNames: [], fileTotalSize: 0, filePath: cbFilePath()};

            var items = e.originalEvent.dataTransfer.items;
            if (items) {
                //폴더 및 파일 다중 드롭처리
                var dirs = [];

                //드롭 파일 우선 처리
                $.map(items, function (item) {
                    if (item.kind !== 'file') {
                        return;
                    }

                    var entry = item.webkitGetAsEntry();
                    if (entry.isFile) {
                        var file = item.getAsFile();
                        file.filePath = cbFilePath();
                        if (file.filePath != '/') {
                            file.fullPath = file.filePath + '/' + file.name;
                        } else {
                            file.fullPath = file.filePath + file.name;
                        }
                        file.uuid = self.genUUID();
                        uploadItems.files.push(file);
                        uploadItems.fileTotalSize += file.size;
                        uploadItems.dropFileNames.push(file.name);
                        uploadItems.dropFiles.push(file);
                    } else if (entry.isDirectory) {
                        dirs.push(entry);
                        uploadItems.dropFileNames.push(entry.name);
                        uploadItems.dropFiles.push(entry);
                    }
                });

                //드롭 폴더 처리
                if (dirs.length == 0) {
                    //파일만 드롭후
                    uploadFileCallBack.apply(pageObj, [uploadItems]);
                } else {
                    //폴더 드룹후
                    $.map(dirs, function (dir, index) {
                        self.traverse_directory(dir, uploadItems).then(function (entries) {
                            if (index == dirs.length - 1) {
                                $.map(uploadItems.file_entries, function (entry, idx) {
                                    //entry to file
                                    self.entry_file(entry).then(function (fileArray) {
                                        var file = fileArray[0];
                                        file.filePath = cbFilePath();
                                        if (file.filePath != '/') {
                                            file.fullPath = file.filePath + entry.fullPath;
                                        } else {
                                            file.fullPath = entry.fullPath;
                                        }
                                        file.uuid = self.genUUID();
                                        uploadItems.files.push(file);
                                        uploadItems.fileTotalSize += file.size;
                                        if (idx == uploadItems.file_entries.length - 1) {
                                            uploadItems.file_entries = [];
                                            uploadFileCallBack.apply(pageObj, [uploadItems]);
                                        }
                                    });
                                });
                            }
                        });
                    });
                }
            } else {
                //IE 파일만가능
                var files = e.originalEvent.dataTransfer.files;
                if (files.length < 1) return;

                for (var i = 0; i < files.length; i++) {
                    var file = files[i];
                    file.filePath = cbFilePath();
                    if (file.filePath != '/') {
                        file.fullPath = file.filePath + '/' + file.name;
                    } else {
                        file.fullPath = file.filePath + file.name;
                    }
                    file.uuid = self.genUUID();
                    uploadItems.files.push(file);
                    uploadItems.fileTotalSize += file.size;
                    uploadItems.dropFileNames.push(file.name);
                    uploadItems.dropFiles.push(file);
                }
                //파일 드롭후
                //console.debug(uploadItems);
                uploadFileCallBack.apply(pageObj, [uploadItems]);
            }
        });
    },
    //폴더 드롭시 폴더탐색(Sync)
    traverse_directory: function (entry, uploadItems) {
        var self = this;
        var reader = entry.createReader();
        // Resolved when the entire directory is traversed
        return new Promise(function (resolve_directory) {
            var iteration_attempts = [];
            (function read_entries() {
                // According to the FileSystem API spec, readEntries() must be called until
                // it calls the callback with an empty array.  Seriously??
                reader.readEntries(function (entries) {
                    if (!entries.length) {
                        // Done iterating this particular directory
                        resolve_directory(Promise.all(iteration_attempts));
                    } else {
                        // Add a list of promises for each directory entry.  If the entry is itself
                        // a directory, then that promise won't resolve until it is fully traversed.
                        iteration_attempts.push(
                            Promise.all(
                                entries.map(function (entry) {
                                    //console.debug(entry);
                                    if (entry.isFile) {
                                        // DO SOMETHING WITH FILES
                                        uploadItems.file_entries.push(entry);
                                        return entry;
                                    } else {
                                        // DO SOMETHING WITH DIRECTORIES
                                        uploadItems.dir.push(entry.fullPath);
                                        return self.traverse_directory(entry, uploadItems);
                                    }
                                })
                            )
                        );
                        // Try calling readEntries() again for the same dir, according to spec
                        read_entries();
                    }
                });
            })();
        });
    },
    //파일 객체(Sync)
    entry_file: function (entry) {
        // Resolved when the entire directory is traversed
        return new Promise(function (resolve_entry) {
            var iteration_attempts = [];
            (function read_entry() {
                entry.file(function (file) {
                    iteration_attempts.push(file);
                    resolve_entry(Promise.all(iteration_attempts));
                });
            })();
        });
    }
};
