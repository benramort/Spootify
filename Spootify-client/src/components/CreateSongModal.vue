<!-- src/components/Modal.vue -->
<template>
    <transition name="fade">
        <div v-if="isOpen" class="modal-overlay" @click="close">
            <div class="modal-content" @click.stop>
                <slot></slot>
            </div>
        </div>
    </transition>
  </template>
  
  <script>
  export default {
    props: {
      isOpen: Boolean
    },
    emits: ["close"],
    methods: {
      close() {
        this.$emit("close");
      }
    }
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .modal-content {
    padding: 20px;
    border-radius: 5px;
    transform: scale(0.9);
    transition: transform 0.3s ease-out;
    transition: transform 0.3s ease-in;
  }

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease-out;
  transition: opacity 0.15s ease-in;
}

.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: scale(1);
}
  </style>
  