import { Edit, Trash2 } from "lucide-react";

export interface ColumnDef<T> {
  header: string;
  accessor: keyof T;
}

interface DataTableProps<T> {
  data: T[];
  columns: ColumnDef<T>[];
  onEdit?: (row: T) => void;
  onDelete?: (row: T) => void;
}

/**
 * Tabela genérica reutilizável — usada em todos os CRUDs (Alunos, Usuários, etc).
 */
export default function DataTable<T extends { id: number | string }>({
  data,
  columns,
  onEdit,
  onDelete
}: DataTableProps<T>) {
  return (
    <div className="overflow-x-auto bg-white rounded-xl border border-slate-200 shadow">
      <table className="w-full text-left text-sm border-collapse">
        <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 text-xs uppercase">
          <tr>
            {columns.map((col) => (
              <th key={String(col.accessor)} className="px-4 py-2 font-semibold">
                {col.header}
              </th>
            ))}
            {(onEdit || onDelete) && <th className="px-4 py-2">Ações</th>}
          </tr>
        </thead>

        <tbody>
          {data.length > 0 ? (
            data.map((row) => (
              <tr
                key={String(row.id)}
                className="border-b border-slate-100 hover:bg-slate-50 transition"
              >
                {columns.map((col) => (
                  <td key={String(col.accessor)} className="px-4 py-2 text-slate-800">
                    {String(row[col.accessor] ?? "")}
                  </td>
                ))}

                {(onEdit || onDelete) && (
                  <td className="px-4 py-2">
                    <div className="flex items-center gap-2">
                      {onEdit && (
                        <button
                          onClick={() => onEdit(row)}
                          className="p-1 rounded hover:bg-slate-200 text-slate-600"
                          title="Editar"
                        >
                          <Edit size={16} />
                        </button>
                      )}
                      {onDelete && (
                        <button
                          onClick={() => onDelete(row)}
                          className="p-1 rounded hover:bg-red-100 text-red-600"
                          title="Excluir"
                        >
                          <Trash2 size={16} />
                        </button>
                      )}
                    </div>
                  </td>
                )}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={columns.length + ((onEdit || onDelete) ? 1 : 0)}
                className="px-4 py-6 text-center text-slate-400 text-sm"
              >
                Nenhum registro encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
