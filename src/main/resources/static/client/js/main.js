(function ($) {
    "use strict";

    // Spinner
    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 1);
    };
    spinner(0);

    // Fixed Navbar
    $(window).scroll(function () {
        if ($(window).width() < 992) {
            if ($(this).scrollTop() > 55) {
                $('.fixed-top').addClass('shadow');
            } else {
                $('.fixed-top').removeClass('shadow');
            }
        } else {
            if ($(this).scrollTop() > 55) {
                $('.fixed-top').addClass('shadow').css('top', 0);
            } else {
                $('.fixed-top').removeClass('shadow').css('top', 0);
            }
        }
    });

    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 300) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });

    // Testimonial carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 2000,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav : true,
        navText : [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0:{items:1},
            576:{items:1},
            768:{items:1},
            992:{items:2},
            1200:{items:2}
        }
    });

    // vegetable carousel
    $(".vegetable-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1500,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav : true,
        navText : [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0:{items:1},
            576:{items:1},
            768:{items:2},
            992:{items:3},
            1200:{items:4}
        }
    });

    // Modal Video
    $(document).ready(function () {
        var $videoSrc;
        $('.btn-play').click(function () {
            $videoSrc = $(this).data("src");
        });
        console.log($videoSrc);

        $('#videoModal').on('shown.bs.modal', function (e) {
            $("#video").attr('src', $videoSrc + "?autoplay=1&modestbranding=1&showinfo=0");
        });

        $('#videoModal').on('hide.bs.modal', function (e) {
            $("#video").attr('src', $videoSrc);
        });

        // add active class to header
        const navElement = $("#navbarCollapse");
        const currentUrl = window.location.pathname;
        navElement.find('a.nav-link').each(function() {
            const link = $(this);
            const href = link.attr('href');

            if (href === currentUrl) {
               link.addClass('active');
            } else {
               link.removeClass('active');
            }
        });
    });

    // Xử lý cập nhật giá dựa trên input quantity
    function updatePrice(input, newVal) {
        // Đảm bảo số lượng không được bằng 0
        if (newVal <= 0) {
            newVal = 1;
            input.val(newVal);
        }
        // Restrict quantity to a maximum of 998
        if (newVal > 998) {
            newVal = 998;
            input.val(newVal);
            showToast('Số lượng tối đa là 998 sản phẩm.', 'error');
        }

        // set from index
        const index = input.attr("data-cart-detail-index");
        const el = document.getElementById(`cartDetails${index}.quantity`);
        $(el).val(newVal);

        // get price
        const price = input.attr("data-cart-detail-price");
        const id = input.attr("data-cart-detail-id");
        const oldVal = parseFloat(input.attr("data-old-value") || newVal);

        // Tính toán sự thay đổi
        const change = newVal - oldVal;

        // Cập nhật giá trị cũ
        input.attr("data-old-value", newVal);

        // Cập nhật giá tiền từng sản phẩm
        const priceElement = $(`p[data-cart-detail-id='${id}']`);
        if (priceElement) {
            const newPrice = +price * newVal;
            priceElement.text(formatCurrency(newPrice.toFixed(2)) + " đ");
        }

        // Cập nhật tổng tiền
        const totalPriceElement = $(`p[data-cart-total-price]`);
        if (totalPriceElement && totalPriceElement.length) {
            const currentTotal = totalPriceElement.first().attr("data-cart-total-price");
            let newTotal = +currentTotal;

            // Cập nhật tổng tiền dựa trên sự thay đổi
            newTotal = (+currentTotal) + change * (+price);

            // update
            totalPriceElement?.each(function (index, element) {
                // update text
                $(totalPriceElement[index]).text(formatCurrency(newTotal.toFixed(2)) + " đ");

                // update data-attribute
                $(totalPriceElement[index]).attr("data-cart-total-price", newTotal);
            });
        }
    }

    // Chọn all buttons bên trong phần tử có class 'quantity'
    $('.quantity button').on('click', function () {
        var button = $(this);
        var oldValue = button.parent().parent().find('input').val();
        var newVal;

        if (button.hasClass('btn-plus')) {
            newVal = parseFloat(oldValue) + 1;
            // Prevent incrementing beyond 998
            if (newVal > 998) {
                newVal = 998;
                showToast('Số lượng tối đa là 998 sản phẩm.', 'error');
            }
        } else {
            if (oldValue > 1) {
                newVal = parseFloat(oldValue) - 1;
            } else {
                newVal = 1;
            }
        }

        const input = button.parent().parent().find('input');
        input.val(newVal);
        updatePrice(input, newVal);
    });

    // Xử lý sự kiện khi người dùng nhập trực tiếp vào input
    $('.quantity input').on('input', function() {
        const input = $(this);
        let value = input.val().trim();

        // Chỉ giữ lại các ký tự số
        value = value.replace(/[^0-9]/g, '');

        // Restrict input to 998
        if (value > 998) {
            value = 998;
            input.val(value);
            showToast('Số lượng tối đa là 998 sản phẩm.', 'error');
        }

        // Đặt lại giá trị đã lọc
        input.val(value);
    });

    // Xử lý khi người dùng hoàn thành việc nhập
    $('.quantity input').on('blur', function() {
        const input = $(this);
        let value = input.val().trim();

        // Nếu input trống hoặc giá trị là 0, đặt lại thành 1
        if (value === '' || parseInt(value) <= 0) {
            value = '1';
            input.val(value);
        }
        // Restrict to maximum 998
        if (parseInt(value) > 998) {
            value = '998';
            input.val(value);
            showToast('Số lượng tối đa là 998 sản phẩm.', 'error');
        }

        // Cập nhật giá
        updatePrice(input, parseInt(value));
    });

    // Xử lý khi người dùng nhấn Enter
    $('.quantity input').on('keypress', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            const input = $(this);
            let value = input.val().trim();

            // Nếu input trống hoặc giá trị là 0, đặt lại thành 1
            if (value === '' || parseInt(value) <= 0) {
                value = '1';
                input.val(value);
            }
            // Restrict to maximum 998
            if (parseInt(value) > 998) {
                value = '998';
                input.val(value);
                showToast('Số lượng tối đa là 998 sản phẩm.', 'error');
            }

            // Cập nhật giá
            updatePrice(input, parseInt(value));

            // Bỏ focus khỏi input
            input.blur();
        }
    });

    function formatCurrency(value) {
        const formatter = new Intl.NumberFormat('vi-VN', {
            style: 'decimal',
            minimumFractionDigits: 0,
        });

        let formatted = formatter.format(value);
        formatted = formatted.replace(/\./g, ',');
        return formatted;
    }

    // handle filter with all checkbox, radio
    $(document).ready(function () {
        function updateUrl() {
            let factoryArr = [];
            let targetArr = [];
            let priceArr = [];

            $("#factory-filter .form-check-input:checked").each(function () {
                factoryArr.push($(this).val());
            });

            $("#target-filter .form-check-input:checked").each(function () {
                targetArr.push($(this).val());
            });

            $("#price-filter .form-check-input:checked").each(function () {
                priceArr.push($(this).val());
            });

            let sortValue = $('input[name="radio-sort"]:checked').val();

            const currentUrl = new URL(window.location.href);
            const searchParams = currentUrl.searchParams;

            searchParams.set('page', '1');
            searchParams.set('sort', sortValue);

            if (factoryArr.length > 0) {
                searchParams.set('factory', factoryArr.join(','));
            } else {
                searchParams.delete('factory');
            }

            if (targetArr.length > 0) {
                searchParams.set('target', targetArr.join(','));
            } else {
                searchParams.delete('target');
            }

            if (priceArr.length > 0) {
                searchParams.set('price', priceArr.join(','));
            } else {
                searchParams.delete('price');
            }

            window.location.href = currentUrl.toString();
        }

        $('#factory-filter .form-check-input, #target-filter .form-check-input, #price-filter .form-check-input, #sort-filter .form-check-input').on('change', function () {
            updateUrl();
        });

        const params = new URLSearchParams(window.location.search);

        if (params.has('factory')) {
            const factories = params.get('factory').split(',');
            factories.forEach(factory => {
                $(`#factory-filter input[value="${factory}"]`).prop('checked', true);
            });
        }

        if (params.has('target')) {
            const targets = params.get('target').split(',');
            targets.forEach(target => {
                $(`#target-filter input[value="${target}"]`).prop('checked', true);
            });
        }

        if (params.has('price')) {
            const prices = params.get('price').split(',');
            prices.forEach(price => {
                $(`#price-filter input[value="${price}"]`).prop('checked', true);
            });
        }

        if (params.has('sort')) {
            const sortValue = params.get('sort');
            $(`input[name="radio-sort"][value="${sortValue}"]`).prop('checked', true);
        }
    });

    // Thêm thuộc tính data-old-value cho input khi trang được tải
    $(document).ready(function () {
        $('.quantity input').each(function() {
            const input = $(this);
            const currentValue = input.val();
            input.attr('data-old-value', currentValue);
        });
    });

    // handle filter with filter btn
/*    $('#btn-filter').click(function (event) {
        event.preventDefault();

        let factoryArr = [];
        let targetArr = [];
        let priceArr = [];

        // Lấy giá trị từ các checkbox được chọn
        $("#factory-filter .form-check-input:checked").each(function () {
            factoryArr.push($(this).val());
        });

        $("#target-filter .form-check-input:checked").each(function () {
            targetArr.push($(this).val());
        });

        $("#price-filter .form-check-input:checked").each(function () {
            priceArr.push($(this).val());
        });

        // Lấy giá trị sắp xếp
        let sortValue = $('input[name="radio-sort"]:checked').val();

        const currentUrl = new URL(window.location.href);
        const searchParams = currentUrl.searchParams;

        // Đặt lại trang về 1 khi lọc
        searchParams.set('page', '1');

        // Cập nhật tham số sắp xếp
        searchParams.set('sort', sortValue);

        // Xử lý bộ lọc hãng sản xuất
        if (factoryArr.length > 0) {
            searchParams.set('factory', factoryArr.join(','));
        } else {
            searchParams.delete('factory'); // Xóa tham số nếu không có lựa chọn
        }

        // Xử lý bộ lọc mục đích sử dụng
        if (targetArr.length > 0) {
            searchParams.set('target', targetArr.join(','));
        } else {
            searchParams.delete('target');
        }

        // Xử lý bộ lọc mức giá
        if (priceArr.length > 0) {
            searchParams.set('price', priceArr.join(','));
        } else {
            searchParams.delete('price');
        }

        // Cập nhật URL và tải lại trang
        window.location.href = currentUrl.toString();
    });

    // Xử lý tự động tích các checkbox khi trang được tải
    $(document).ready(function () {
        const params = new URLSearchParams(window.location.search);

        // Tích các checkbox cho 'factory'
        if (params.has('factory')) {
            const factories = params.get('factory').split(',');
            factories.forEach(factory => {
                $(`#factory-filter input[value="${factory}"]`).prop('checked', true);
            });
        }

        // Tích các checkbox cho 'target'
        if (params.has('target')) {
            const targets = params.get('target').split(',');
            targets.forEach(target => {
                $(`#target-filter input[value="${target}"]`).prop('checked', true);
            });
        }

        // Tích các checkbox cho 'price'
        if (params.has('price')) {
            const prices = params.get('price').split(',');
            prices.forEach(price => {
                $(`#price-filter input[value="${price}"]`).prop('checked', true);
            });
        }

        // Tích radio cho 'sort'
        if (params.has('sort')) {
            const sortValue = params.get('sort');
            $(`input[name="radio-sort"][value="${sortValue}"]`).prop('checked', true);
        }
    });*/

    // NEW: Toast Notification System with Duplicate Prevention
    function showToast(message, type = 'info') {
        // Create toast container if it doesn't exist
        let toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'toast-container';
            document.body.appendChild(toastContainer);
        }

        // Check for existing toast with the same message and type
        const existingToast = Array.from(toastContainer.querySelectorAll('.toast')).find(toast => {
            const toastMessage = toast.querySelector('span').textContent;
            const toastType = toast.classList.contains(`toast-${type}`);
            return toastMessage === message && toastType;
        });

        if (existingToast) {
            // Reset the timeout of the existing toast to keep it visible
            clearTimeout(existingToast.dataset.timeoutId);
            existingToast.classList.remove('show');
            setTimeout(() => {
                existingToast.classList.add('show');
            }, 10); // Brief flicker to indicate refresh
            existingToast.dataset.timeoutId = setTimeout(() => {
                existingToast.classList.remove('show');
                setTimeout(() => {
                    existingToast.remove();
                }, 300);
            }, 3000).toString();
            return;
        }

        // Create new toast element
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <span>${message}</span>
            <button class="toast-close">×</button>
        `;

        // Append toast to container
        toastContainer.appendChild(toast);

        // Trigger slide-in animation
        setTimeout(() => {
            toast.classList.add('show');
        }, 100);

        // Auto-remove toast after 3 seconds
        const timeoutId = setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 3000);
        toast.dataset.timeoutId = timeoutId.toString();

        // Handle close button click
        toast.querySelector('.toast-close').addEventListener('click', () => {
            clearTimeout(toast.dataset.timeoutId);
            toast.classList.remove('show');
            setTimeout(() => {
                toast.remove();
            }, 300);
        });
    }

    // NEW: Inject toast styles
    $(document).ready(function() {
        const style = document.createElement('style');
        style.innerHTML = `
            .toast-container {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                max-width: 300px;
            }
            .toast {
                background: #fff;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                padding: 12px 16px;
                margin-bottom: 10px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                opacity: 0;
                transform: translateX(100%);
                transition: all 0.3s ease;
                font-size: 14px;
                color: #333;
            }
            .toast.show {
                opacity: 1;
                transform: translateX(0);
            }
            .toast-error {
                border-left: 4px solid #dc3545;
            }
            .toast-close {
                background: none;
                border: none;
                font-size: 18px;
                cursor: pointer;
                color: #666;
                margin-left: 10px;
            }
            .toast-close:hover {
                color: #333;
            }
        `;
        document.head.appendChild(style);
    });

})(jQuery);