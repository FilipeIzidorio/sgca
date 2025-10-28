interface ConfirmDialogProps {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

/**
 * Modal de confirmação simples — usado para exclusões e ações críticas.
 */
export default function ConfirmDialog({
  title,
  message,
  confirmLabel = "Confirmar",
  cancelLabel = "Cancelar",
  onConfirm,
  onCancel
}: ConfirmDialogProps) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/40 backdrop-blur-sm z-50">
      <div className="bg-white w-full max-w-sm rounded-xl shadow-lg border border-slate-200 p-6 animate-fadeIn">
        <h2 className="text-lg font-semibold text-slate-800 mb-2">{title}</h2>
        <p className="text-slate-600 text-sm">{message}</p>

        <div className="flex justify-end gap-2 mt-6 text-sm">
          <button
            onClick={onCancel}
            className="px-4 py-2 rounded-lg border border-slate-300 text-slate-600 hover:bg-slate-100 transition"
          >
            {cancelLabel}
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 transition"
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
