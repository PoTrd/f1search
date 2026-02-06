import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';

interface ActionLogEntry {
  action: string;
  status: 'pending' | 'success' | 'error';
  time: string;
}

const ADMIN_USER = 'admin';
const ADMIN_PASS = 'admin';
const SESSION_KEY = 'f1search-admin';

@Component({
  selector: 'app-admin-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.scss',
})
export class AdminPage {
  username = '';
  password = '';
  error = signal<string | null>(null);
  isAuthed = signal(sessionStorage.getItem(SESSION_KEY) === 'true');
  logs = signal<ActionLogEntry[]>([]);
  busy = signal(false);

  readonly showLogin = computed(() => !this.isAuthed());
  readonly showAdmin = computed(() => this.isAuthed());
  readonly hasLogs = computed(() => this.logs().length > 0);

  constructor(private readonly api: ApiService) {}

  login(): void {
    if (this.username === ADMIN_USER && this.password === ADMIN_PASS) {
      this.error.set(null);
      this.isAuthed.set(true);
      sessionStorage.setItem(SESSION_KEY, 'true');
      return;
    }
    this.error.set('Invalid credentials. Try admin/admin.');
  }

  logout(): void {
    sessionStorage.removeItem(SESSION_KEY);
    this.isAuthed.set(false);
    this.username = '';
    this.password = '';
  }

  runAction(action: string, request: () => void): void {
    if (this.busy()) {
      return;
    }

    this.busy.set(true);
    this.prependLog(action, 'pending');

    request();
  }

  startIndexing(): void {
    this.runAction('Start indexing', () => {
      this.api.startIndexing().subscribe({
        next: () => this.completeAction('Start indexing', 'success'),
        error: () => this.completeAction('Start indexing', 'error')
      });
    });
  }

  startCrawling(): void {
    this.runAction('Start crawling', () => {
      this.api.startCrawling().subscribe({
        next: () => this.completeAction('Start crawling', 'success'),
        error: () => this.completeAction('Start crawling', 'error')
      });
    });
  }

  stopCrawling(): void {
    this.runAction('Stop crawling', () => {
      this.api.stopCrawling().subscribe({
        next: () => this.completeAction('Stop crawling', 'success'),
        error: () => this.completeAction('Stop crawling', 'error')
      });
    });
  }

  private prependLog(action: string, status: ActionLogEntry['status']): void {
    const time = new Date().toLocaleTimeString();
    this.logs.set([{ action, status, time }, ...this.logs()]);
  }

  private completeAction(action: string, status: ActionLogEntry['status']): void {
    this.busy.set(false);
    this.prependLog(action, status);
  }
}
