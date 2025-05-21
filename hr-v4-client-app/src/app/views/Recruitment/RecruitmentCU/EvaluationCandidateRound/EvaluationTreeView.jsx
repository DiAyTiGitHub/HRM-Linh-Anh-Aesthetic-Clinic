import { useState } from "react";

// Component chính để hiển thị TreeView
export default function EvaluationTreeView({ data }) {
    // Xử lý dữ liệu từ API response
    const evaluationValues = data?.evaluationValues || [];
    console.log(data);
    // Xây dựng cấu trúc cây từ dữ liệu phẳng
    const treeItems = buildTreeData(evaluationValues);

    return (
        <div className='p-4 bg-gray-50 rounded-lg'>
            <h2 className='text-xl font-bold mb-4'>Evaluation Tree View</h2>
            <div className='border border-gray-200 rounded-lg bg-white p-4'>
                {Object.values(treeItems).map((item) => (
                    <TreeNode key={item.id} node={item} level={0} />
                ))}
            </div>
        </div>
    );
}

// Component hiển thị từng node trong cây
function TreeNode({ node, level }) {
    const [expanded, setExpanded] = useState(true);
    const hasChildren = node.children && node.children.length > 0;

    // Xác định nội dung hiển thị
    const displayName = node.item ? node.item.name || node.name : node.name || "Unnamed Item";

    // Tính padding dựa trên level
    const paddingLeft = `${level * 16}px`;

    return (
        <div className='tree-node'>
            <div
                className={`flex items-center py-2 ${level > 0 ? "border-t border-gray-100" : ""}`}
                style={{ paddingLeft }}>
                {hasChildren && (
                    <button
                        onClick={() => setExpanded(!expanded)}
                        className='mr-2 w-6 h-6 flex items-center justify-center text-gray-500 hover:bg-gray-100 rounded'>
                        {expanded ? "−" : "+"}
                    </button>
                )}

                {!hasChildren && <span className='w-6 h-6 mr-2'></span>}

                <div className='flex-1'>
                    <span className={`${node.contentType === "TITLE" ? "font-semibold" : ""}`}>{displayName}</span>
                </div>
            </div>

            {expanded && hasChildren && (
                <div className='children'>
                    {node.children.map((child) => (
                        <TreeNode key={child.id} node={child} level={level + 1} />
                    ))}
                </div>
            )}
        </div>
    );
}

// Hàm xây dựng cấu trúc cây từ dữ liệu phẳng
function buildTreeData(evaluationValues) {
    // Tạo map để lưu trữ các item theo id
    const itemMap = {};

    // Xử lý từng item trong evaluationValues
    evaluationValues.forEach((evalValue) => {
        if (evalValue.item) {
            const itemData = evalValue.item;

            // Lưu thông tin vào map với id làm key
            itemMap[itemData.id] = {
                ...itemData,
                evaluationValue: evalValue,
                children: [],
            };
        }
    });

    // Cây kết quả
    const rootItems = {};

    // Xây dựng cây
    Object.values(itemMap).forEach((item) => {
        if (item.parentId && itemMap[item.parentId]) {
            // Nếu có parentId và parent tồn tại, thêm vào children của parent
            itemMap[item.parentId].children.push(item);

            // Sắp xếp children theo numberOrder
            itemMap[item.parentId].children.sort((a, b) => a.numberOrder - b.numberOrder);
        } else {
            // Đây là node gốc
            rootItems[item.id] = item;
        }
    });

    console.log("rootItems: ",rootItems);

    return rootItems;
}
