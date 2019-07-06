package org.cfpa.i18nupdatemod.download;

import org.cfpa.i18nupdatemod.I18nUpdateMod;
import org.cfpa.i18nupdatemod.git.ResourcePackRepository;
import org.eclipse.jgit.lib.ProgressMonitor;

public class RepoUpdateManager implements IDownloadManager {
    private ResourcePackRepository repo;
    private DownloadStatus status = DownloadStatus.IDLE;
    private boolean done = false;
    private ProgressMonitor monitor;
    private boolean cancelled = false;
    private String taskTitle = "等待...";
    private float completePercentage = 0;

    public void update() {
        DownloadWindow window = new DownloadWindow(this);
        window.showWindow();
        this.start("I18n-Download-Thread");
        while (this.getStatus() == DownloadStatus.DOWNLOADING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
        }
    }

    class simpleProgressMonitor implements ProgressMonitor {
        int totalTasks = 1;
        int totalWork = 1;
        int curTask = 0;
        int completed = 0;

        @Override
        public void beginTask(String title, int totalWork) {
            // TODO L10n
            taskTitle = title;
            completed = 0;
            this.totalWork = totalWork;
        }

        @Override
        public void endTask() {
            curTask += 1;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void start(int totalTasks) {
            this.totalTasks = totalTasks;
        }

        @Override
        public void update(int completed) {
            this.completed += completed;
            completePercentage = (float) this.completed / (float) totalWork;
        }

    }

    public RepoUpdateManager(ResourcePackRepository repo) {
        this.repo = repo;
        monitor = new simpleProgressMonitor();
    }

    @Override
    public void cancel() {
        status = DownloadStatus.CANCELED;
        this.cancelled = true;
    }

    @Override
    public void background() {
        status = DownloadStatus.BACKGROUND;

    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    public DownloadStatus getStatus() {
        if ((status == DownloadStatus.DOWNLOADING || status == DownloadStatus.BACKGROUND) && this.done) {
            status = DownloadStatus.SUCCESS;
        }
        return status;
    }

    @Override
    public float getCompletePercentage() {
        return completePercentage;
    }

    @Override
    public String getTaskTitle() {
        return taskTitle;
    }

    @Override
    public void start(String threadName) {
        status = DownloadStatus.DOWNLOADING;
        Thread downloadThread = new Thread(() -> {
            try {
                repo.fetch(monitor);
                repo.sparseCheckout(repo.getSubPaths(), monitor);
                repo.close();
                this.done = true;
            } catch (Throwable e) {
                I18nUpdateMod.logger.error("Error while downloading: ", e);
            }
        }, threadName);
        downloadThread.start();

    }

}
