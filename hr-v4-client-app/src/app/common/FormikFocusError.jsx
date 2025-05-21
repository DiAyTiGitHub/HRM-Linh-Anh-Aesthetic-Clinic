import { useFormikContext } from 'formik';
import { useEffect } from 'react';
import { toast } from 'react-toastify';
import lodash from 'lodash';

const FormikFocusError = () => {
  const { errors, isSubmitting, isValidating, touched } = useFormikContext();

  useEffect(() => {
    if (isSubmitting && !isValidating && errors && Object.keys(errors).length > 0) {
      const errorPaths = getLeaves(errors);

      let firstErrorPath = errorPaths.find(path => lodash.get(errors, path) && lodash.get(touched, path));

      if (!firstErrorPath && errorPaths.length > 0) {
        firstErrorPath = errorPaths[0];
      }

      if (firstErrorPath) {
        const errorMessage = lodash.get(errors, firstErrorPath);
        const fieldName = getFieldName(firstErrorPath);

        toast.warning(`${fieldName}: ${errorMessage}`, { autoClose: 3000, toastId: 'formik-focus-error' });

        const errorElement = getErrorElement(firstErrorPath);
        if (errorElement) {
          setTimeout(() => {
            focusTabOfElement(errorElement);

            setTimeout(() => {
              errorElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
              errorElement.focus({ preventScroll: true });
            }, 300);
          }, 300);
        }
      }
    }
  }, [errors, isSubmitting, isValidating, touched]);

  return null;
};

const getLeaves = (tree) => {
  const leaves = [];
  const walk = (obj, path = '') => {
    for (let key in obj) {
      if (!obj.hasOwnProperty(key)) continue;
      const newPath = Array.isArray(obj) ? `${path}[${key}]` : (path ? `${path}.${key}` : key);

      if (typeof obj[key] === 'object' && obj[key] !== null) {
        walk(obj[key], newPath);
      } else {
        leaves.push(newPath);
      }
    }
  };
  walk(tree);
  return leaves;
};

const getErrorElement = (fieldPath) => {
  const id = fieldPath.replace(/\[(\d+)\]/g, '-$1').replace(/\./g, '-');
  return document.getElementById(id) ||
      document.querySelector(`[name="${fieldPath}"]`) ||
      document.getElementById(`label-for-${fieldPath}`);
};

const getFieldName = (fieldPath) => {
  const id = fieldPath.replace(/\[(\d+)\]/g, '-$1').replace(/\./g, '-');
  const label = document.querySelector(`label[for="${id}"]`);
  if (label) {
    return label.innerText?.trim() || label.textContent?.trim() || id;
  }

  const lastPart = fieldPath.split(/[\.\[\]]+/).filter(Boolean).pop();
  return lastPart
      .replace(/_/g, ' ')
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase());
};

// 🎯 Không dùng store - Tìm tab bằng DOM
const focusTabOfElement = (element) => {
  if (!element) return;

  const tabPane = element.closest('.tab-pane-custom');
  if (tabPane) {
    const tabPanelId = tabPane.getAttribute('id');
    if (tabPanelId) {
      const tabButton = document.querySelector(`[role="tab"][aria-controls="${tabPanelId}"]`);
      if (tabButton) {
        tabButton.click(); // giả lập click tab
      }
    }
  }
};

export default FormikFocusError;
