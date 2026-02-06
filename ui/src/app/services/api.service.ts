import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';

export interface SearchResultItem {
  title: string;
  description: string | null;
  url: string;
}

export interface SearchResponseDto {
  query: string;
  results: SearchResultItem[];
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  // On n'utilise plus environment.apiBaseUrl ici pour forcer le passage par le proxy
  // Le proxy est configuré sur le préfixe '/v1'
  private readonly apiPrefix = '/v1';

  constructor(private readonly http: HttpClient) {}

  search(query: string): Observable<SearchResponseDto> {
    // Appel vers '/v1/search' -> Le proxy redirige vers 'http://localhost:8080/api/v1/search'
    return this.http.get<any>(`${this.apiPrefix}/search`, {
        params: { query },
        responseType: 'json'
      })
      .pipe(
        // DEBUG: Affiche ce que le serveur renvoie réellement dans la console F12
        tap(raw => console.log('🔍 Payload reçu:', raw)),

        map((raw) => {
          // Gestion sécurisée du parsing
          const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;

          // Logique pour trouver le tableau de résultats peu importe la structure
          let results: SearchResultItem[] = [];

          if (Array.isArray(parsed)) {
            results = parsed;
          } else if (Array.isArray(parsed?.results)) {
            results = parsed.results;
          } else if (Array.isArray(parsed?.data)) {
            results = parsed.data; // Convention courante
          }

          return {
            query: parsed?.query ?? query,
            results: results
          } as SearchResponseDto;
        })
      );
  }

  startIndexing(): Observable<void> {
    // Attention: Si ton backend pour ces actions n'est pas sous /v1,
    // il faudra ajouter une entrée dans le proxy.conf.json ou ajuster l'URL ici.
    // Je suppose ici qu'elles sont aussi sous /v1 (ex: /api/v1/actions/...)
    return this.http.post<void>(`${this.apiPrefix}/actions/indexing/start`, {});
  }

  startCrawling(): Observable<void> {
    return this.http.post<void>(`${this.apiPrefix}/actions/crawling/start`, {});
  }

  stopCrawling(): Observable<void> {
    return this.http.post<void>(`${this.apiPrefix}/actions/crawling/stop`, {});
  }
}
