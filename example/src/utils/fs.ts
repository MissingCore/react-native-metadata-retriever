import { Directory, Paths } from 'expo-file-system';

export const ImageDirectory = Paths.join(Paths.document, 'images');

export function getImageDirectory() {
  const imgDir = new Directory(ImageDirectory);
  if (!imgDir.exists) imgDir.create();
  return imgDir;
}
