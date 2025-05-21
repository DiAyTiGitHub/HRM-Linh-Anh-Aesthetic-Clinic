import { observer } from "mobx-react";
import React, { memo, useState } from "react";

const SalaryItemFormulaInput = () => {
    // Dữ liệu mẫu
    const [salaryData, setSalaryData] = useState([
        { id: 1, name: "Lương cơ bản", value: "10,000,000" },
        { id: 2, name: "Thưởng", value: "2,000,000" },
        { id: 3, name: "Phụ cấp", value: "1,000,000" },
    ]);

    // Theo dõi trạng thái chỉnh sửa
    const [editingId, setEditingId] = useState(null);
    const [tempValue, setTempValue] = useState("");

    // Bắt đầu chỉnh sửa
    const startEditing = (id, currentValue) => {
        setEditingId(id);
        setTempValue(currentValue);
    };

    // Lưu chỉnh sửa
    const saveEdit = (id) => {
        setSalaryData((prevData) =>
            prevData.map((item) =>
                item.id === id ? { ...item, value: tempValue } : item
            )
        );
        setEditingId(null);
    };

    // Hủy chỉnh sửa
    const cancelEdit = () => {
        setEditingId(null);
        setTempValue("");
    };

    return (
        <div>
            <h2>Quản lý thành phần lương</h2>
            <table border="1" style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                    <tr>
                        <th>Thành phần</th>
                        <th>Giá trị</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {salaryData.map((item) => (
                        <tr key={item.id}>
                            <td>{item.name}</td>
                            <td>
                                {editingId === item.id ? (
                                    <input
                                        type="text"
                                        value={tempValue}
                                        onChange={(e) => setTempValue(e.target.value)}
                                    />
                                ) : (
                                    item.value
                                )}
                            </td>
                            <td>
                                {editingId === item.id ? (
                                    <>
                                        <button onClick={() => saveEdit(item.id)}>Lưu</button>
                                        <button onClick={cancelEdit}>Hủy</button>
                                    </>
                                ) : (
                                    <button onClick={() => startEditing(item.id, item.value)}>
                                        Chỉnh sửa
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default memo(observer(SalaryItemFormulaInput));