(function () {
    'use strict';

    function initMobileMenu() {
        const toggle = document.querySelector('.site-header__menu-toggle');
        const navigation = document.querySelector('#site-header-navigation');

        if (!toggle || !navigation) {
            return;
        }

        toggle.addEventListener('click', function () {
            const isOpen = toggle.getAttribute('aria-expanded') === 'true';

            toggle.setAttribute('aria-expanded', String(!isOpen));
            toggle.setAttribute(
                'aria-label',
                isOpen ? 'Open menu' : 'Close menu'
            );

            navigation.classList.toggle('is-open', !isOpen);
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initMobileMenu);
    } else {
        initMobileMenu();
    }
})();