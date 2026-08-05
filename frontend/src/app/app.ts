import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { FilterStatusPipe } from './filter-status.pipe';

type Payment = { id: string; idempotencyKey: string; amount: number; currency: string; authorizedAt: string };
type Message = {
  id: string; aggregateId: string; eventType: string; payload: string; idempotencyKey: string;
  status: 'PENDING' | 'CLAIMED' | 'PUBLISHED' | 'FAILED'; attemptCount: number;
  nextAttemptAt: string; leasedUntil?: string; publishedAt?: string; lastError?: string;
};
type Evidence = { idempotencyKey: string; eventType: string; consumedAt: string };
type Snapshot = { payments: Payment[]; messages: Message[]; consumedEvents: Evidence[] };

@Component({
  selector: 'app-root',
  imports: [CommonModule, ReactiveFormsModule, FilterStatusPipe],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private readonly http = inject(HttpClient);
  readonly loading = signal(false);
  readonly notice = signal('');
  readonly snapshot = signal<Snapshot>({ payments: [], messages: [], consumedEvents: [] });
  readonly form = new FormGroup({
    amount: new FormControl(42, { nonNullable: true, validators: [Validators.required, Validators.min(0.01)] }),
    currency: new FormControl('BRL', { nonNullable: true, validators: [Validators.required, Validators.pattern(/^[A-Za-z]{3}$/)] }),
  });

  constructor() { this.refresh(); }

  authorize(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.notice.set('');
    this.http.post('/api/payments', this.form.getRawValue(), {
      headers: { 'Idempotency-Key': crypto.randomUUID() },
    }).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: () => this.refresh(),
      error: (problem) => this.notice.set(problem.error?.detail ?? 'Não foi possível autorizar o pagamento.'),
    });
  }

  failNext(): void {
    this.http.post('/api/operations/fail-next-publication', null).subscribe({
      next: () => this.notice.set('A próxima publicação falhará de forma controlada.'),
      error: () => this.notice.set('Não foi possível armar a falha controlada.'),
    });
  }

  refresh(): void {
    this.http.get<Snapshot>('/api/operations/snapshot').subscribe({
      next: (snapshot) => this.snapshot.set(snapshot),
      error: () => this.notice.set('API indisponível. Inicie o ambiente local e tente novamente.'),
    });
  }
}
