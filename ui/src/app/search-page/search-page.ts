import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, SearchResultItem } from '../services/api.service';

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-page.html',
  styleUrl: './search-page.scss',
})
export class SearchPage {
  query = '';
  results = signal<SearchResultItem[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  lastQuery = signal('');

  readonly resultsCount = computed(() => this.results().length);
  readonly errorMessage = computed(() => this.error());
  readonly showSummary = computed(() => !this.loading() && !!this.lastQuery());
  readonly showEmpty = computed(
    () => !this.loading() && !this.results().length && !!this.lastQuery() && !this.error()
  );

  constructor(private readonly api: ApiService) {}

  submit(): void {
    const trimmed = this.query.trim();
    if (!trimmed) {
      this.error.set('Type a query to begin.');
      return;
    }

    this.error.set(null);
    this.loading.set(true);
    this.lastQuery.set(trimmed);

    this.api.search(trimmed).subscribe({
      next: (response) => {
        this.results.set(response.results ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Search failed. Please check the server and try again.');
        this.loading.set(false);
      }
    });
  }

  usePrompt(prompt: string): void {
    this.query = prompt;
    this.submit();
  }
}
